package com.KooKPaP.server.domain.member.service;

import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.common.service.RedisService;
import com.KooKPaP.server.global.jwt.JwtTokenProvider;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import com.KooKPaP.server.global.social.KakaoProfile;
import com.KooKPaP.server.global.social.OauthToken;
import com.KooKPaP.server.global.social.OauthTokenInfoRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class KakaoAuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final String clientSecret;
    private final String clientId;

    @Autowired
    public KakaoAuthService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, RedisService redisService,
                            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String clientSecret,
                            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String clientId) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
    }

    public OauthToken getKakaoAccessToken (String code, Role role) {
        RestTemplate rt = new RestTemplate();

        String customerUri = "http://localhost:8080/auth/login/kakao/customer";
        String managerUri="http://localhost:8080/auth/login/kakao/manager";
        String redirectionUri = role == Role.CUSTOMER ? customerUri : managerUri;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectionUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    public KakaoProfile findProfile(OauthToken oauthToken) {

        String accessToken = oauthToken.getAccess_token();

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        try {
            // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
            ResponseEntity<KakaoProfile> kakaoProfileResponse = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoProfileRequest,
                    KakaoProfile.class
            );

            System.out.println(kakaoProfileResponse.getBody());
            return kakaoProfileResponse.getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AUTH_EXPIRED_KAKAO_ACCESS_TOKEN);
        }
    }


    public JwtTokenDto saveUserAndGetToken(OauthToken oauthToken, Role role) {

        KakaoProfile profile = findProfile(oauthToken);

        System.out.println(profile);

        Member member = null;

        try {
            member = memberRepository.findByEmail(profile.getKakao_account().getEmail()).get();
            // email이 같은데 KAKAO 사용자가 아니면, 이메일 중복으로 회원가입하려고 하는것. 따라서 Exception
            if(member.getType()!=LoginType.KAKAO) throw new CustomException(ErrorCode.AUTH_DUPLICATED_EMAIL);
        } catch (NoSuchElementException e) {

            member = Member.builder()
                    .name(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
                    .type(LoginType.KAKAO)
                    .role(role)
                    .build();

            memberRepository.save(member);
        }
        redisService.setValue(member.getId().toString(), oauthToken, 50L, TimeUnit.DAYS); //OauthToken의 refreshToken이 대충 59일동안 유효함.

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateJwtTokenDto(member);
        redisService.setValue(jwtTokenDto.getRefreshToken(), member.getId().toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void serviceLogout(PrincipalDetails principalDetails){
        // 서비스 로그아웃(카카오)
        // 카카오 accessToken 만료
        Member member = principalDetails.getMember();

        if(member.getType() != LoginType.KAKAO) return;

        OauthToken oauthToken = getOauthToken(member.getId());
        if(oauthToken == null) return;

        System.out.println(oauthToken);

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        HttpEntity<MultiValueMap<String, String>> logoutRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    logoutRequest,
                    String.class
            );

            System.out.println("회원번호 " + response.getBody() + " 로그아웃");
            redisService.deleteValue(member.getId().toString());

        } catch (Exception e) {
            return;
        }
    }

    public boolean isExpired(Long id) {

        // OauthToken 정보를 받아와서 만료됐는지 검사
        // 만료되었으면 true, 아직 유효하면 false
        OauthToken oauthToken = (OauthToken) redisService.getValue(id.toString());
        if (oauthToken == null) throw new CustomException(ErrorCode.AUTH_EXPIRED_OAUTH_TOKEN);

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<OauthTokenInfoRes> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/access_token_info",
                    HttpMethod.GET,
                    request,
                    OauthTokenInfoRes.class
            );
            return false;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && ex.getResponseBodyAsString().contains("-401")) {
                return true;
            } else {
                throw new CustomException(ErrorCode.AUTH_KAKAO_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public OauthToken getRefresh(Long id) {

        // OauthToken을 refresh하는 메서드.
        OauthToken oauthToken = (OauthToken) redisService.getValue(id.toString());
        if(oauthToken == null) throw new CustomException(ErrorCode.AUTH_EXPIRED_OAUTH_TOKEN);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", oauthToken.getRefresh_token());
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> refreshRequest = new HttpEntity<>(params, headers);

        ResponseEntity<OauthToken> refreshResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                refreshRequest,
                OauthToken.class
        );
        OauthToken refreshOauthToken = refreshResponse.getBody();
        refreshOauthToken.setScope(oauthToken.getScope());

        if (refreshOauthToken.getRefresh_token() == null) {
            refreshOauthToken.setRefresh_token(oauthToken.getRefresh_token());
        }
        redisService.setValue(id.toString(), refreshOauthToken, 50L, TimeUnit.DAYS);

        return refreshOauthToken;
    }

    public OauthToken getOauthToken(Long id) {
        OauthToken oauthToken;
        if (isExpired(id)) {
            oauthToken = getRefresh(id);
        }else {
            oauthToken = (OauthToken) redisService.getValue(id.toString());
        }

        return oauthToken;
    }
}
