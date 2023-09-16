package com.KooKPaP.server.global.jwt;

import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Request Header에서 JWT 토큰 꺼내기
            String jwt = resolveToken(request);

            // 유효성검사
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Black List에 올라와 있는지 검사.
                if("Deprecated".equals(redisService.getValue(jwt))){
                    request.setAttribute("exception", ErrorCode.AUTH_DEPRECATED_ACCESS_TOKEN);
                    filterChain.doFilter(request, response);
                    return;
                }

                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else request.setAttribute("exception", ErrorCode.JWT_ABSENCE_TOKEN);
        } catch (CustomException e) {
            request.setAttribute("exception", e.getErrorCode());
        }

        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(JwtAttribute.HeaderString);

        if (StringUtils.hasText(token) && token.startsWith(JwtAttribute.TOKEN_PREFIX)) {
            return token.replace(JwtAttribute.TOKEN_PREFIX, "");
        }
        return null;
    }
}
