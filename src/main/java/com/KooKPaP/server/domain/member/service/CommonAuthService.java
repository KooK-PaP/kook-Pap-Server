package com.KooKPaP.server.domain.member.service;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.common.service.RedisService;
import com.KooKPaP.server.global.jwt.JwtAttribute;
import com.KooKPaP.server.global.jwt.JwtTokenProvider;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonAuthService {
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public JwtTokenDto reissue(String oldRefreshToken) {
        String value = (String) redisService.getValue(oldRefreshToken);
        System.out.println(oldRefreshToken);
        System.out.println(value);
        if ("Deprecated".equals(value) || !jwtTokenProvider.validateToken(oldRefreshToken)) {
            // refresh 토큰이 블랙리스트에 존재하는지 검사 & 유효성 검사
            throw new CustomException(ErrorCode.AUTH_DEPRECATED_REFRESH_TOKEN);
        }

        Long id = Long.valueOf(value);
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_USER_NOT_FOUND));

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateJwtTokenDto(member);
        redisService.setValue(oldRefreshToken, "Deprecated", 7L, TimeUnit.DAYS);
        redisService.setValue(jwtTokenDto.getRefreshToken(), id.toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void deprecateTokens(JwtTokenDto jwtTokenDto) {
        String accessToken = jwtTokenDto.getAccessToken();
        String refreshToken = jwtTokenDto.getRefreshToken();

        redisService.setValue(accessToken, "Deprecated", 30L, TimeUnit.MINUTES);
        redisService.setValue(refreshToken, "Deprecated", 7L, TimeUnit.DAYS);
    }
}