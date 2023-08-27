package com.KooKPaP.server.global.jwt;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    // 토큰 유효성 검사,발급 등등
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 180; // 3시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    private final String key;
    private final MemberRepository memberRepository;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, MemberRepository memberRepository) {
        this.key = secretKey;
        this.memberRepository = memberRepository;
    }

    public JwtTokenDto generateJwtTokenDto(Member member) {
        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = JWT.create()
                .withSubject(member.getEmail())                                       // payload "sub": "member.email"
//                .withClaim(AUTHORITIES_KEY, "ROLE_" + member.getRole())             // payload "auth": "ROLE_MANAGER"  << 이거 굳이 필요없어 보여서 뺌
                .withClaim("id", member.getId())                                // payload "id": "member.id"
                .withClaim("name", member.getName())                            // payload "name": "member.name"
                .withExpiresAt(accessTokenExpiresIn)
                .sign(Algorithm.HMAC256(key));                                        // header "alg": "HS256"

        // Refresh Token 생성
        String refreshToken = JWT.create()
                .withExpiresAt(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .sign(Algorithm.HMAC256(key));

        return JwtTokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // Authentication 객체 만들기
        Long memberId = JWT.require(Algorithm.HMAC256(key)).build().verify(accessToken).getClaim("id").asLong();

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );

        PrincipalDetails principalDetails = new PrincipalDetails(member);

        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                "",
                principalDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {
        // 예외 처리 로직은 나중에 수정 예정
        try {
            JWT.require(Algorithm.HMAC256(key)).build().verify(token);
            return true;
        } catch (SignatureVerificationException e) {
            throw new CustomException(ErrorCode.JWT_INVALID_TOKEN);
        } catch (TokenExpiredException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED_TOKEN);
        } catch (AlgorithmMismatchException | JWTDecodeException e) {
            throw new CustomException(ErrorCode.JWT_UNSUPPORTED_TOKEN);
        } catch (JWTVerificationException e) {
            throw new CustomException(ErrorCode.JWT_WRONG_TOKEN);
        }
    }
}
