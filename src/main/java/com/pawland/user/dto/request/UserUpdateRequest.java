package com.pawland.user.dto.request;

import com.pawland.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    private String introduce = "";

    private String profileImage = "";

    @Builder
    public UserUpdateRequest(String nickname, String introduce, String profileImage) {
        this.nickname = nickname;
        this.introduce = introduce;
        this.profileImage = profileImage;
    }

    public User toUser() {
        return User.builder()
            .nickname(nickname)
            .introduce(introduce)
            .profileImage(profileImage)
            .build();
    }
}
