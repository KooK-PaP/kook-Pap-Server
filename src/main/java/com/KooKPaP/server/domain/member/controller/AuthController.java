package com.KooKPaP.server.domain.member.controller;

import com.KooKPaP.server.domain.member.dto.request.*;
import com.KooKPaP.server.domain.member.service.CommonAuthService;
import com.KooKPaP.server.domain.member.service.GeneralAuthService;
import com.KooKPaP.server.domain.member.service.KakaoAuthService;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.JwtAttribute;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import com.KooKPaP.server.global.jwt.tokenDto.JwtTokenDto;
import com.KooKPaP.server.global.social.OauthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final GeneralAuthService generalAuthService;
    private final KakaoAuthService kakaoAuthService;
    private final CommonAuthService commonAuthService;

    @PostMapping("/email")
    public ApplicationResponse<?> verifyEmail(@RequestBody EmailReq emailReq) {
        String email = emailReq.getEmail();
        if (!generalAuthService.isDuplicatedEmail(email)) {
            generalAuthService.sendAuthCode(email);
        }
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, null);
    }

    @PostMapping("/code")
    public ApplicationResponse<?> verifyCode(@Valid @RequestBody VerifyAuthCodeReq verifyAuthCodeReq) {
        generalAuthService.verifyAuthCode(verifyAuthCodeReq.getEmail(), verifyAuthCodeReq.getAuthCode());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, null);
    }

    @PostMapping("/signup")
    public ApplicationResponse<?> signup(@Valid @RequestBody SignupReq signupReq) {
        generalAuthService.signup(signupReq);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, null);
    }

    @PostMapping("/login/general")
    public ApplicationResponse<JwtTokenDto> generalLogin(@Valid @RequestBody GeneralLoginReq loginReqDto) {
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
    public ApplicationResponse<?> logout(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              HttpServletRequest request, @RequestBody RefreshTokenReq refreshTokenReq) {

        String accessToken = request.getHeader(JwtAttribute.HeaderString).replace(JwtAttribute.TOKEN_PREFIX, "");

        commonAuthService.deprecateTokens(accessToken, refreshTokenReq.getRefreshToken());
        kakaoAuthService.serviceLogout(principalDetails);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, null);
    }

    @PostMapping("/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody RefreshTokenReq request) {
        JwtTokenDto jwtTokenDto = commonAuthService.reissue(request.getRefreshToken());
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }
}
