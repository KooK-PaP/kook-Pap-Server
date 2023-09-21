package com.KooKPaP.server.global.jwt;

import com.KooKPaP.server.global.common.dto.ApplicationErrorResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 인증 관련 에러 처리, 401
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Object exception = request.getAttribute("exception");

        if (exception instanceof ErrorCode) {
            ErrorCode errorCode = (ErrorCode) exception;
            setResponse(response,errorCode);

            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        ApplicationErrorResponse errorResponse = new ApplicationErrorResponse(errorCode);
        ObjectMapper objectMapper = new ObjectMapper();
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);
    }
}
