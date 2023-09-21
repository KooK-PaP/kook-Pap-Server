package com.KooKPaP.server.domain.member.controller;

import com.KooKPaP.server.domain.member.dto.request.EmailReq;
import com.KooKPaP.server.domain.member.dto.request.MemberUpdateReq;
import com.KooKPaP.server.domain.member.dto.request.PasswordReq;
import com.KooKPaP.server.domain.member.dto.request.RefreshTokenReq;
import com.KooKPaP.server.domain.member.dto.response.MemberInfoRes;
import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.service.CommonAuthService;
import com.KooKPaP.server.domain.member.service.GeneralAuthService;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final GeneralAuthService generalAuthService;
    private final CommonAuthService commonAuthService;


    @GetMapping("/me")
    public ApplicationResponse<MemberInfoRes> getMyInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
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

        if(!generalAuthService.isDuplicatedEmail(emailReq.getEmail()))
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
