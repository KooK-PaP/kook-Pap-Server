package com.KooKPaP.server.global.social;

import lombok.Getter;
@Getter
public class OauthTokenInfoRes {
    private Integer code;
    private String msg;

    private Long id;
    private Integer expires_in;
    private Integer app_id;
}