package com.pawland.user.dto.request;

import com.pawland.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "유저 정보 수정 요청")
public class UserInfoUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    private String userDesc = "";

    private String profileImage = "";

    @Builder
    public UserInfoUpdateRequest(String nickname, String userDesc, String profileImage) {
        this.nickname = nickname;
        this.userDesc = userDesc;
        this.profileImage = profileImage;
    }

    public User toUser() {
        return User.builder()
            .nickname(nickname)
            .introduce(userDesc)
            .profileImage(profileImage)
            .build();
    }
}
