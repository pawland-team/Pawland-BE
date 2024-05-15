package com.pawland.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "이메일 인증용 메일 요청")
public class SendVerificationCodeRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    public SendVerificationCodeRequest(String email) {
        this.email = email;
    }
}
