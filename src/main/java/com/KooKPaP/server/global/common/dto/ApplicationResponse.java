package com.KooKPaP.server.global.common.dto;

import com.KooKPaP.server.global.common.exception.ErrorCode;
import lombok.*;

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


    public static <T> ApplicationResponse<T> ok(ErrorCode errorCode, T object) {
        ApplicationResponse<T> applicationResponse = new ApplicationResponse<>();

        applicationResponse.setStatus(errorCode.getHttpStatus().value());
        applicationResponse.setMessage(errorCode.getMessage());
        applicationResponse.setCode(errorCode.getCode());
        applicationResponse.setObject(object);

        return applicationResponse;
    }

}