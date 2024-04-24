package com.pawland.user.dto.response;

import com.pawland.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UserInfoResponse {

    private Long id;

    private String profileImage;

    private String nickname;

    private String email;

    private String userDesc;

    private double stars = 0.0; // 구현중

    @Builder
    public UserInfoResponse(User user, double stars) {
        this.id = user.getId();
        this.profileImage = user.getProfileImage();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.userDesc = user.getIntroduce();
        this.stars = stars;
    }
}
