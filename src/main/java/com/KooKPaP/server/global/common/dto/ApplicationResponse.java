package com.KooKPaP.server.global.common.dto;

import com.KooKPaP.server.global.common.exception.ErrorCode;
import lombok.*;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse<T> {
    private int status;
    private String message;
    private int code;
    private T object;

    public ApplicationResponse(ErrorCode errorCode, T object) {
        this.status = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.object = object;
    }

    public static <T> ApplicationResponse<T> ok(ErrorCode errorCode, T object) {
        return  new ApplicationResponse<>(errorCode,object);
    }
}