package com.KooKPaP.server.domain.member.controller;

import com.KooKPaP.server.domain.member.dto.request.*;
import com.KooKPaP.server.domain.member.entity.Role;
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

    @PostMapping("/signup/customer")
    public ApplicationResponse<?> customerSignup(@Valid @RequestBody SignupReq signupReq) {
        // 소비자용 회원가입.
        generalAuthService.signup(signupReq, Role.CUSTOMER);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, null);
    }

    @PostMapping("/signup/manager")
    public ApplicationResponse<?> managerSignup(@Valid @RequestBody SignupReq signupReq) {
        // 가게주인용 회원가입
        generalAuthService.signup(signupReq, Role.MANAGER);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, null);
    }

    @PostMapping("/login/general")
    public ApplicationResponse<JwtTokenDto> generalLogin(@Valid @RequestBody GeneralLoginReq loginReqDto) {
        JwtTokenDto jwtTokenDto = generalAuthService.login(loginReqDto);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @GetMapping("/login/kakao/customer")
    public ApplicationResponse<JwtTokenDto> kakaoCustomerLogin(@RequestParam("code") String code) {
        // 변경대상
        OauthToken oauthToken = kakaoAuthService.getKakaoAccessToken(code, Role.CUSTOMER);
        JwtTokenDto jwtTokenDto = kakaoAuthService.saveUserAndGetToken(oauthToken, Role.CUSTOMER);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @GetMapping("/login/kakao/manager")
    public ApplicationResponse<JwtTokenDto> kakaoManagerLogin(@RequestParam("code") String code) {
        // 변경대상
        OauthToken oauthToken = kakaoAuthService.getKakaoAccessToken(code, Role.MANAGER);
        JwtTokenDto jwtTokenDto = kakaoAuthService.saveUserAndGetToken(oauthToken, Role.MANAGER);
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
