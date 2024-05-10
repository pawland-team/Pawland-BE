package com.pawland.user.dto.response;

import com.pawland.user.domain.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private Double star;
    private int reviewCount;

    private UserResponse(Long id, String email, String nickname, String profileImage, Double star, int reviewCount) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.star = star;
        this.reviewCount = reviewCount;
    }

    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getProfileImage(), user.getStar(), user.getReviewCount());
    }


}
