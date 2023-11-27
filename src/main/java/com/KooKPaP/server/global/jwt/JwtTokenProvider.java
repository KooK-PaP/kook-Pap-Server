package com.KooKPaP.server.global.jwt;

import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    // 토큰 유효성 검사,발급 등등
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
        Date accessTokenExpiresIn = new Date(now + JwtAttribute.ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = JWT.create()
                .withSubject(member.getEmail())                                       // payload "sub": "member.email"
                .withClaim("id", member.getId())                                // payload "id": "member.id"
                .withClaim("authority", member.getRole().toString())            // payload "authority": "member.role"
                .withClaim("type", member.getType().toString())                 // payload "type": "member.type"
                .withExpiresAt(accessTokenExpiresIn)
                .sign(Algorithm.HMAC256(key));                                        // header "alg": "HS256"

        // Refresh Token 생성
        String refreshToken = JWT.create()
                .withExpiresAt(new Date(now + JwtAttribute.REFRESH_TOKEN_EXPIRE_TIME))
                .sign(Algorithm.HMAC256(key));

        return JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // Authentication 객체 만들기
        DecodedJWT claims = JWT.require(Algorithm.HMAC256(key)).build().verify(accessToken);

        Long memberId = claims.getClaim("id").asLong();
        String authority = claims.getClaim("authority").asString();
        LoginType type = claims.getClaim("type").as(LoginType.class);

        PrincipalDetails principalDetails = PrincipalDetails.builder()
                .id(memberId)
                .authority(new SimpleGrantedAuthority("ROLE_" + authority))
                .type(type)
                .build();

        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                "",
                principalDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {
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
