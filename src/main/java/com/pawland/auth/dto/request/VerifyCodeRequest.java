package com.pawland.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "발급된 인증 번호로 이메일 인증 요청")
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

