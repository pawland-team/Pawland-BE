package com.pawland.user.dto.response;

import com.pawland.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(name = "유저 정보 조회 응답")
public class UserInfoResponse {

    private Long id;
    private String profileImage;
    private String nickname;
    private String email;
    private String userDesc;
    private String loginType;
    private double stars;
    private int reviewCount;

    @Builder
    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.profileImage = user.getProfileImage();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.userDesc = user.getIntroduce();
        this.loginType = user.getType().value();
        this.stars = user.getStar();
        this.reviewCount = user.getReviewCount();
    }
}
