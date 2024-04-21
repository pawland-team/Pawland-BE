package com.pawland.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyCodeRequest {

    @NotBlank(message = "인증 번호를 입력해주세요.")
    private String code;

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Builder
    public VerifyCodeRequest(String code, String email) {
        this.code = code;
        this.email = email;
    }
}

