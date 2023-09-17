package com.KooKPaP.server.domain.member.controller;

import com.KooKPaP.server.domain.member.dto.request.*;
import com.KooKPaP.server.domain.member.dto.response.AuthMeRes;
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
    public ApplicationResponse<String> verifyCode(@Valid @RequestBody VerifyAuthCodeReq verifyAuthCodeReq) {
        generalAuthService.verifyAuthCode(verifyAuthCodeReq.getEmail(), verifyAuthCodeReq.getAuthCode());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "인증이 완료되었습니다.");
    }

    @PostMapping("/signup")
    public ApplicationResponse<String> signup(@Valid @RequestBody SignupReq signupReq) {
        generalAuthService.signup(signupReq);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, "회원가입이 완료되었습니다.");
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
    public ApplicationResponse<String> logout(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              HttpServletRequest request, @RequestBody RefreshTokenReq refreshTokenReq) {

        String accessToken = request.getHeader(JwtAttribute.HeaderString).replace(JwtAttribute.TOKEN_PREFIX, "");

        commonAuthService.deprecateTokens(accessToken, refreshTokenReq.getRefreshToken());
        kakaoAuthService.serviceLogout(principalDetails);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "로그아웃 되었습니다.");
    }

    @PostMapping("/reissue")
    public ApplicationResponse<JwtTokenDto> reissue(@RequestBody Map<String, String> request) {
        JwtTokenDto jwtTokenDto = commonAuthService.reissue(request.get("refreshToken"));
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, jwtTokenDto);
    }

    @PostMapping("/withdrawal")
    public ApplicationResponse<String> withdrawal(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  HttpServletRequest request, @RequestBody RefreshTokenReq refreshTokenReq) {
        commonAuthService.deleteMember(principalDetails);
        String accessToken = request.getHeader(JwtAttribute.HeaderString).replace(JwtAttribute.TOKEN_PREFIX, "");
        commonAuthService.deprecateTokens(accessToken, refreshTokenReq.getRefreshToken());

        // 지금은 member만 삭제시키고, 추후 관련 엔티티들도 전부 삭제하는 로직으로 업데이트 하겠음.

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "회원 탈퇴가 완료되었습니다.");
    }

    @GetMapping("/me")
    public ApplicationResponse<AuthMeRes> authMe(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long id = principalDetails.getMember().getId();
        AuthMeRes authMeRes = commonAuthService.getMemberInfo(id);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, authMeRes);
    }

    @PutMapping("/update")
    public ApplicationResponse<AuthMeRes> memberUpdate(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestBody MemberUpdateReq memberUpdateReq) {
        Long id = principalDetails.getMember().getId();
        commonAuthService.updateMember(id, memberUpdateReq);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, commonAuthService.getMemberInfo(id));
    }

    @PatchMapping("/update/email")
    public ApplicationResponse<AuthMeRes> memberEmailUpdate(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                            @RequestBody EmailUpdateReq emailUpdateReq) {
        Long id = principalDetails.getMember().getId();
        String newEmail = emailUpdateReq.getEmail();
        commonAuthService.updateEmail(id, newEmail);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, commonAuthService.getMemberInfo(id));
    }

    @PostMapping("/password")
    public ApplicationResponse<String> verifyPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestBody PasswordReq passwordReq) {
        Long id = principalDetails.getMember().getId();
        commonAuthService.verifyPassword(id, passwordReq.getPassword());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "비밀번호가 일치합니다.");
    }

    @PatchMapping("/password")
    public ApplicationResponse<String> updatePassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestBody PasswordReq passwordReq) {
        Long id = principalDetails.getMember().getId();
        commonAuthService.updatePassword(id, passwordReq.getPassword());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, "비밀번호가 변경되었습니다.");
    }
}
