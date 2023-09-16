package com.KooKPaP.server.domain.member.service;

import com.KooKPaP.server.domain.member.dto.request.GeneralLoginReqDto;
import com.KooKPaP.server.domain.member.dto.request.SignupReqDto;
import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.common.service.MailService;
import com.KooKPaP.server.global.common.service.RedisService;
import com.KooKPaP.server.global.jwt.JwtTokenProvider;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class GeneralAuthService {
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public boolean isDuplicatedEmail(String email) {
        if(memberRepository.existsByEmail(email))
            throw new CustomException(ErrorCode.AUTH_DUPLICATED_EMAIL);
        return false;
    }

    @Async
    public void sendAuthCode(String email) {
        // 인증 코드 전송후, 해당 인증 코드를 redis에 30분간 저장.
        try {
            String authCode = mailService.sendAuthCode(email);
            redisService.setValue(email, authCode, 30L, TimeUnit.MINUTES);
        } catch (Throwable e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public void verifyAuthCode(String email, String userAuthCode) {
        String authCode = (String) redisService.getValue(email);
        // redis에 인증코드가 없다면 -> 시간이 오래되서 다시 발급받아야함
        if(authCode==null) throw new CustomException(ErrorCode.AUTH_EXPIRED_AUTH_CODE);
        // redis에서 email에 저장된 상태가 "verified" : 이미 인증되었음.
        if(authCode.equals("verified"))throw new CustomException(ErrorCode.AUTH_VERIFIED_AUTH_CODE);
        // 인증코드가 다를경우, 실패
        if(!authCode.equals(userAuthCode)) throw new CustomException(ErrorCode.AUTH_INVALID_AUTH_CODE);

        // 인증이 완료되면 잠시 redis에 email이 인증되었다고 저장.
        redisService.setValue(email, "verified", 30L, TimeUnit.MINUTES);
    }

    public void signup(SignupReqDto requestDto) {
        // email이 인증되지 않았다면, Exception
        if(!"verified".equals(redisService.getValue(requestDto.getEmail()))) throw new CustomException(ErrorCode.AUTH_UNVERIFIED_EMAIL);

        Member member = Member.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .address(requestDto.getAddress())
                .type(LoginType.GENERAL)
                .role(requestDto.getRole())
                .build();

        redisService.deleteValue(requestDto.getEmail());
        memberRepository.save(member);
    }

    public JwtTokenDto login(GeneralLoginReqDto loginReqDto) {
        Member member = memberRepository.findByEmail(loginReqDto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_INVALID_LOGIN_INFO));

        if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_INVALID_LOGIN_INFO);
        }

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateJwtTokenDto(member);
        // redis에 <refreshToken, member.id>로 사용자 정보 저장.
        redisService.setValue(jwtTokenDto.getRefreshToken(), member.getId(), 7L, TimeUnit.MINUTES);

        return jwtTokenDto;
    }
}
