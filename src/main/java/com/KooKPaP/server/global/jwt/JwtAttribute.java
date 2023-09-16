package com.KooKPaP.server.global.jwt;

public interface JwtAttribute {
    String HeaderString = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 180L; // 3시간
    Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7L; // 7일
}
