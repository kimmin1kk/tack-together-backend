package com.dnlab.tacktogetherbackend.auth.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private final JwtTokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = tokenProvider.resolveToken(httpServletRequest);

        // 유효성 검증
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            // 토큰에서 유저네임, 권한을 받아 스프링 시큐리티 유저를 만들어 Authentication 반환
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            // 해당 스프링 시큐리티 유저를 시큐리티 컨텍스트에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context 에 " + authentication.getName() + " 인증 정보를 저장했습니다.");
        } else {
            log.debug("유효한 JWT 토큰이 없습니다");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
