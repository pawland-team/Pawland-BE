package com.pawland.global.config.security.filter;

import com.pawland.global.config.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RequestMatcher excludeUrlsMatcher;

    public JwtAuthFilter(JwtUtils jwtUtils, RequestMatcher excludeUrlsMatcher) {
        this.jwtUtils = jwtUtils;
        this.excludeUrlsMatcher = excludeUrlsMatcher;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludeUrlsMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String jwt = jwtUtils.getJwtFromCookie(cookies);

        Authentication authentication = jwtUtils.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
