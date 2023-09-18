package com.KooKPaP.server.domain.member.controller;

import com.KooKPaP.server.domain.member.dto.request.*;
import com.KooKPaP.server.domain.member.dto.response.MemberInfoRes;
import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.service.CommonAuthService;
import com.KooKPaP.server.domain.member.service.GeneralAuthService;
import com.KooKPaP.server.domain.member.service.KakaoAuthService;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.CustomException;
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

    @PostMapping("/withdrawal")
    public ApplicationResponse<?> withdrawal(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  HttpServletRequest request, @RequestBody RefreshTokenReq refreshTokenReq) {
        commonAuthService.deleteMember(principalDetails);
        String accessToken = request.getHeader(JwtAttribute.HeaderString).replace(JwtAttribute.TOKEN_PREFIX, "");
        commonAuthService.deprecateTokens(accessToken, refreshTokenReq.getRefreshToken());

        // 지금은 member만 삭제시키고, 추후 관련 엔티티들도 전부 삭제하는 로직으로 업데이트 하겠음.

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, null);
    }

    @GetMapping("/me")
    public ApplicationResponse<MemberInfoRes> authMe(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long id = principalDetails.getMember().getId();
        MemberInfoRes memberInfoRes = commonAuthService.getMemberInfo(id);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, memberInfoRes);
    }

    @PutMapping("/update")
    public ApplicationResponse<MemberInfoRes> memberUpdate(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                           @RequestBody MemberUpdateReq memberUpdateReq) {
        Long id = principalDetails.getMember().getId();
        commonAuthService.updateMember(id, memberUpdateReq);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, commonAuthService.getMemberInfo(id));
    }

    @PatchMapping("/update/email")
    public ApplicationResponse<MemberInfoRes> memberEmailUpdate(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                @RequestBody EmailReq emailReq) {
        Long id = principalDetails.getMember().getId();
        String newEmail = emailReq.getEmail();

        if(generalAuthService.isDuplicatedEmail(emailReq.getEmail()))
            commonAuthService.updateEmail(id, newEmail);

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, commonAuthService.getMemberInfo(id));
    }

    @PostMapping("/password")
    public ApplicationResponse<?> verifyPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestBody PasswordReq passwordReq) {
        if (principalDetails.getMember().getType()!= LoginType.GENERAL)
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOW_FOR_KAKAO_MEMBER);

        Long id = principalDetails.getMember().getId();
        commonAuthService.verifyPassword(id, passwordReq.getPassword());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, null);
    }

    @PatchMapping("/password")
    public ApplicationResponse<?> updatePassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      @RequestBody PasswordReq passwordReq) {
        if (principalDetails.getMember().getType()!= LoginType.GENERAL)
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOW_FOR_KAKAO_MEMBER);

        Long id = principalDetails.getMember().getId();
        commonAuthService.updatePassword(id, passwordReq.getPassword());

        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, null);
    }
}
