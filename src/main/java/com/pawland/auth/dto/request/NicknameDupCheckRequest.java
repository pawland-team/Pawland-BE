package com.pawland.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NicknameDupCheckRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    public NicknameDupCheckRequest(String nickname) {
        this.nickname = nickname;
    }
}
