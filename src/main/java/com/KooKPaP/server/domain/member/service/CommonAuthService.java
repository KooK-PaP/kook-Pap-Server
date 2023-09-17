package com.KooKPaP.server.domain.member.service;

import com.KooKPaP.server.domain.member.dto.request.MemberUpdateReq;
import com.KooKPaP.server.domain.member.dto.response.AuthMeRes;
import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.common.service.RedisService;
import com.KooKPaP.server.global.jwt.JwtTokenProvider;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonAuthService {
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
                () -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateJwtTokenDto(member);
        redisService.setValue(oldRefreshToken, "Deprecated", 7L, TimeUnit.DAYS);
        redisService.setValue(jwtTokenDto.getRefreshToken(), id.toString(), 7L, TimeUnit.DAYS);

        return jwtTokenDto;
    }

    public void deprecateTokens(String accessToken, String refreshToken) {
        redisService.setValue(accessToken, "Deprecated", 30L, TimeUnit.MINUTES);
        redisService.setValue(refreshToken, "Deprecated", 7L, TimeUnit.DAYS);
    }

    public void deleteMember(PrincipalDetails principalDetails) {
        Member member = principalDetails.getMember();
        memberRepository.delete(member);
    }

    public AuthMeRes getMemberInfo(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if(memberOptional.isEmpty()) throw new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND);
        Member member = memberOptional.get();
        return new AuthMeRes().entityToDto(member);
    }

    public void updateMember(Long id, MemberUpdateReq memberUpdateReq) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if(memberOptional.isEmpty()) throw new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND);
        Member member = memberOptional.get();
        member.update(memberUpdateReq);
    }

    public void updateEmail(Long id, String email) {
        // email이 인증되지 않았다면, Exception
        if(!"verified".equals(redisService.getValue(email))) throw new CustomException(ErrorCode.AUTH_UNVERIFIED_EMAIL);
        Optional<Member> memberOptional = memberRepository.findById(id);

        if(memberOptional.isEmpty()) throw new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND);
        Member member = memberOptional.get();

        member.updateEmail(email);
        redisService.deleteValue(email);
    }

    public void verifyPassword(Long id, String password) {
        Optional<Member> memberOptional = memberRepository.findById(id);

        if(memberOptional.isEmpty()) throw new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND);
        Member member = memberOptional.get();

        if(!passwordEncoder.matches(password, member.getPassword()))
            throw new CustomException(ErrorCode.AUTH_WRONG_PASSWORD);

        redisService.setValue(id.toString() + "password", "verified", 30L, TimeUnit.MINUTES);
    }

    public void updatePassword(Long id, String password) {
        Optional<Member> memberOptional = memberRepository.findById(id);

        if(memberOptional.isEmpty()) throw new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND);
        Member member = memberOptional.get();

        if(!"verified".equals(redisService.getValue(id.toString()+"password")))
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);

        member.updatePassword(passwordEncoder.encode(password));
        redisService.deleteValue(id.toString() + "password");
    }
}
