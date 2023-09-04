package com.KooKPaP.server.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    public ErrorCode errorCode;
}
