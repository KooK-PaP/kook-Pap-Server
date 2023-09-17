package com.KooKPaP.server.domain.member.dto.request;

import com.KooKPaP.server.domain.member.entity.Role;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class MemberUpdateReq {
    private String name;
    private Role role;
    @Pattern(regexp = "[가-힣]+도 [가-힣]+시 [가-힣]+구 [가-힣]+로[0-9]+ [0-9]+동 [0-9]+호")
    private String address;
}
