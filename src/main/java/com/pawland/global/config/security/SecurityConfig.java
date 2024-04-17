package com.pawland.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.global.config.security.filter.JsonAuthFilter;
import com.pawland.global.config.security.handler.Http401Handler;
import com.pawland.global.config.security.handler.Http403Handler;
import com.pawland.global.config.security.handler.LoginFailHandler;
import com.pawland.global.config.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/favicon.ico")
            .requestMatchers("/error")
            .requestMatchers(toH2Console()); // TODO: 배포 시 제거
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/signup").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/v3/api-docs/**").permitAll()
                .requestMatchers("/**").permitAll() // TODO: 배포 시 제거
                .anyRequest().authenticated()
            )
//            .addFilterBefore(
//                new JwtAuthFilter(jwtUtils, new ExcludeUrlsRequestMatcher("/api/auth/signup", "/api/auth/login")),
//                UsernamePasswordAuthenticationFilter.class
//            )  // TODO: 배포 시 스웨거 관련 URL 추가 후 활성화
            .addFilterBefore(jsonAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(e -> {
                e.authenticationEntryPoint(new Http401Handler(objectMapper));
                e.accessDeniedHandler(new Http403Handler(objectMapper));
            })
            .csrf(AbstractHttpConfigurer::disable)
            .build();
    }

    public JsonAuthFilter jsonAuthFilter() {
        JsonAuthFilter filter = new JsonAuthFilter("/api/auth/login", objectMapper);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper, jwtUtils));
        filter.setAuthenticationFailureHandler(new LoginFailHandler(objectMapper));
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder(
            16,
            8,
            1,
            32,
            64
        );
    }
}
