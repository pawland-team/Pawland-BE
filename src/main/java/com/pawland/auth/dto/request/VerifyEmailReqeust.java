package com.pawland.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyEmailReqeust {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    public VerifyEmailReqeust(String email) {
        this.email = email;
    }
}
