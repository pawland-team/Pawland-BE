package com.pawland.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(name = "유저 정보 수정 응답")
public class UserInfoUpdateResponse {

    private Long id;
    private String profileImage;
    private String nickname;
    private String userDesc;

    @Builder
    public UserInfoUpdateResponse(Long id, String profileImage, String nickname, String userDesc) {
        this.id = id;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.userDesc = userDesc;
    }
}
