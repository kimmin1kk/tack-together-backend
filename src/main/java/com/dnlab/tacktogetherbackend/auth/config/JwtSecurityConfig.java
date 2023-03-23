package com.dnlab.tacktogetherbackend.auth.config;

import com.dnlab.tacktogetherbackend.auth.common.JwtFilter;
import com.dnlab.tacktogetherbackend.auth.common.JwtTokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Jwt 필터 및 토큰 생성기 설정
 */
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenProvider tokenProvider;

    public JwtSecurityConfig(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        JwtFilter filter = new JwtFilter(tokenProvider);
        builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
