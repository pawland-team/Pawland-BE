package com.pawland.global.utils;

import com.pawland.user.domain.LoginType;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = PawLandMockSecurityContext.class)
public @interface PawLandMockUser {

    String email() default "midcondria@naver.com";

    String nickname() default "나는짱";

    String password() default "asd123123";

    String profileImage() default "";

    String getIntroduce() default "";

    LoginType type() default LoginType.NORMAL;

    String role() default "ROLE_USER";

}