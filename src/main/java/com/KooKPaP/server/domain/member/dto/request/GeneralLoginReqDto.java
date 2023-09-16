package com.KooKPaP.server.domain.member.dto.request;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class GeneralLoginReqDto {
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;
    private String password;
}
