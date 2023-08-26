package com.KooKPaP.server.global.jwt;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    // 토큰 유효성 검사,발금 등등
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 180; // 3시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7주일

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

        // 예외처리 수정 필수
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new RuntimeException("사용자가 존재하지 않습니다.")
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
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        }catch (IllegalArgumentException e){
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
