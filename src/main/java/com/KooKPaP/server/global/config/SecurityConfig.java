package com.KooKPaP.server.global.config;

import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.global.jwt.CustomAccessDeniedHandler;
import com.KooKPaP.server.global.jwt.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtSecurityConfig jwtSecurityConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource());

        http.csrf().disable()  // CSRF 방지기능 비활성화
                // 세션 사용 안함
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // JWT 사용할거라 httpBasic, formLogin 사용 안함
                .httpBasic().disable()
                .formLogin().disable();

        // 여기서 의문... 비로그인 되어 있어도 메뉴 검색정도는 할 수 있게 해야될것같은데,
        // http.anonymous()를 사용해야될까?
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll() // pre-flight 용
                .antMatchers("/v3/api-docs",
                        "/swagger*/**").permitAll()
                .antMatchers("알아서 채우길22").hasAnyRole("MANAGER")
                .anyRequest().authenticated()

                // exception handler 설정
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)

                .and()
                .apply(jwtSecurityConfig);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // cors 설정
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 모든 출처에서 온 요청을 허용
        corsConfiguration.addAllowedOriginPattern("*");
        // 모든 요청 헤더 허용
        corsConfiguration.addAllowedHeader("*");
        // 모든 HTTP 메서드 허용
        corsConfiguration.addAllowedMethod("*");

        // 요청과 응답 간에 인증 정보를 함께 주고 받을 수 있도록 설정
        corsConfiguration.setAllowCredentials(true);

        // 브라우저가 접근 가능한 헤더 설정
        corsConfiguration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers",
                "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
                "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 경로에 대해 앞서 설정한 corsConfiguration를 적용
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
