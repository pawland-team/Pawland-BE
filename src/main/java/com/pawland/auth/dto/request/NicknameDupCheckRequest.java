package com.pawland.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "닉네임 중복 확인 요청")
public class NicknameDupCheckRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    public NicknameDupCheckRequest(String nickname) {
        this.nickname = nickname;
    }
}
