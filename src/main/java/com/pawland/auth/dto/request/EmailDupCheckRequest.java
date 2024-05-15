package com.pawland.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "이메일 중복 확인 요청")
public class EmailDupCheckRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    public EmailDupCheckRequest(String email) {
        this.email = email;
    }
}
