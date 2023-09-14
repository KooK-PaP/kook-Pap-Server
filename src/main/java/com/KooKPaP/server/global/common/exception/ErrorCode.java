package com.KooKPaP.server.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 성공 관련
    SUCCESS_OK(HttpStatus.OK, "성공", 1000),
    SUCCESS_CREATED(HttpStatus.CREATED, "생성 성공", 1001),

    // authentication 관련
    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.", 2001),
    JWT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", 2002),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다.", 2003),
    JWT_WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰 입니다.", 2004),
    JWT_ABSENCE_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다.", 2005),

    // Member 관련
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", 3001),

    // Menu 관련

    // restaurant 관련

    // review 관련
    ;
    private final HttpStatus httpStatus;
    private final String message;
    private final int code;
}