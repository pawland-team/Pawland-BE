package com.pawland.global.config.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

public class ExcludeUrlsRequestMatcher implements RequestMatcher {

    private String[] excludeUrls;

    public ExcludeUrlsRequestMatcher(String... excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return Arrays.stream(excludeUrls)
            .anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, requestURI));
    }
}
