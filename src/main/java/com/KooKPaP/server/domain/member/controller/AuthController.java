package com.KooKPaP.server.domain.member.controller;

import com.KooKPaP.server.domain.member.dto.request.GeneralLoginReqDto;
import com.KooKPaP.server.domain.member.dto.request.SignupReqDto;
import com.KooKPaP.server.domain.member.dto.request.VerifyAuthCodeReqDto;
import com.KooKPaP.server.domain.member.service.CommonAuthService;
import com.KooKPaP.server.domain.member.service.GeneralAuthService;
import com.KooKPaP.server.domain.member.service.KakaoAuthService;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import com.KooKPaP.server.global.social.OauthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final GeneralAuthService generalAuthService;
    private final KakaoAuthService kakaoAuthService;
    private final CommonAuthService commonAuthService;

    @PostMapping("/email")
    public ApplicationResponse<String> verifyEmail(@RequestBody Map<String, String> verifyEmailReq) {
        String email = verifyEmailReq.get("email");
        if (!generalAuthService.isDuplicatedEmail(email)) {
            generalAuthService.sendAuthCode(email);
        }
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "인증 코드가 전송되었습니다.");
    }

    @PostMapping("/code")
    public ApplicationResponse<String> verifyCode(@Valid @RequestBody VerifyAuthCodeReqDto verifyAuthCodeReqDto) {
        generalAuthService.verifyAuthCode(verifyAuthCodeReqDto.getEmail(), verifyAuthCodeReqDto.getAuthCode());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "인증이 완료되었습니다.");
    }

    @PostMapping("/signup")
    public ApplicationResponse<String> signup(@Valid @RequestBody SignupReqDto signupReqDto) {
        generalAuthService.signup(signupReqDto);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login/general")
    public ApplicationResponse<JwtTokenDto> generalLogin(@Valid @RequestBody GeneralLoginReqDto loginReqDto) {
        JwtTokenDto jwtTokenDto = generalAuthService.login(loginReqDto);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @GetMapping("/login/kakao")
    public ApplicationResponse<JwtTokenDto> kakaoLogin(@RequestParam("code") String code) {
        OauthToken oauthToken = kakaoAuthService.getKakaoAccessToken(code);
        JwtTokenDto jwtTokenDto = kakaoAuthService.saveUserAndGetToken(oauthToken);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @PostMapping("/logout")
    public ApplicationResponse<String> logout(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody JwtTokenDto jwtTokenDto) {
        commonAuthService.deprecateTokens(jwtTokenDto);
        kakaoAuthService.serviceLogout(principalDetails);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "로그아웃 되었습니다.");
    }

    @PostMapping("/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody Map<String, String> request) {
        JwtTokenDto jwtTokenDto = commonAuthService.reissue(request.get("refreshToken"));
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }
}
