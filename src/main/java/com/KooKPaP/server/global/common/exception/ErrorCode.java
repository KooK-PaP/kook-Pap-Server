package com.KooKPaP.server.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 성공 관련 (1000번대)
    SUCCESS_OK(HttpStatus.OK, "성공", 1000),
    SUCCESS_CREATED(HttpStatus.CREATED, "생성 성공", 1001),

    // authentication 관련 (2000번대)
    JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.", 2001),
    JWT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", 2002),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다.", 2003),
    JWT_WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰 입니다.", 2004),
    JWT_ABSENCE_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다.", 2005),
    AUTH_DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 email입니다.", 2006),
    AUTH_INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않는 인증코드입니다.", 2007),
    AUTH_EXPIRED_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증코드가 만료되었습니다.", 2008),
    AUTH_VERIFIED_AUTH_CODE(HttpStatus.BAD_REQUEST, "이미 이메일이 인증되었습니다.", 2009),
    AUTH_UNVERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "인증되지 않은 이메일입니다.", 2010),
    AUTH_INVALID_LOGIN_INFO(HttpStatus.NOT_FOUND, "이메일 또는 비밀번호가 잘못되었습니다.", 2011),
    AUTH_EXPIRED_KAKAO_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "카카오 엑세스 토큰이 만료되었습니다.", 2012),
    AUTH_EXPIRED_OAUTH_TOKEN(HttpStatus.BAD_REQUEST, "카카오로부터 정보를 받아올 수 없습니다. 다시 로그인해주세요.", 2013),
    AUTH_KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 서버가 일시적 내부 장애상태 입니다", 2014),
    AUTH_DEPRECATED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "만료된 Refresh 토큰입니다.", 2015),
    AUTH_DEPRECATED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "더 이상 사용되지 않는 Access 토큰입니다", 2016),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다.", 2017),

    // Member 관련 (3000번대)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", 3001),

    // Menu 관련 (4000번대)

    // restaurant 관련 (5000번대)

    // review 관련 (6000번대)

    // 이유 불명 (7000번대)
    UNKNOWN_ERROR(HttpStatus.BAD_GATEWAY, "알 수 없는 오류가 발생했습니다", 7001),
    ;
    private final HttpStatus httpStatus;
    private final String message;
    private final int code;
}
