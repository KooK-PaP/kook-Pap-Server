package com.KooKPaP.server.domain.member.dto.response;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoRes {
    private String name;
    private String email;
    private Role role;
    private String address;
    private LocalDateTime createdAt;

    public MemberInfoRes entityToDto(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
        this.address = member.getAddress();
        this.createdAt = member.getCreatedAt();

        return this;
    }
}
