package com.pawland.global.utils;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.user.domain.LoginType;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PawLandMockSecurityContext implements WithSecurityContextFactory<PawLandMockUser> {

    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(PawLandMockUser annotation) {
        User user = User.builder()
            .email(annotation.email())
            .nickname(annotation.nickname())
            .password(annotation.password())
            .type(annotation.type())
            .profileImage(annotation.profileImage())
            .introduce(annotation.getIntroduce())
            .build();
        userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(user);
        SimpleGrantedAuthority role = new SimpleGrantedAuthority(annotation.role());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal,
            user.getPassword(),
            List.of(role));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        return context;
    }
}
