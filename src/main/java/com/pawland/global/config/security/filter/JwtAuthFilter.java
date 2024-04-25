package com.pawland.global.config.security.filter;

import com.pawland.global.config.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        try {
            String jwt = jwtUtils.getJwtFromCookie(cookies);
            Authentication authentication = jwtUtils.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());
            response.setStatus(SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }
}
