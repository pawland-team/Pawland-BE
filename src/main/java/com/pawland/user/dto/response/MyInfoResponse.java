package com.pawland.user.dto.response;

import lombok.Data;

@Data
public class MyInfoResponse {
    private String nickname;
    private String introduce;
    private Double star;
    private int reviewCount;

    private MyInfoResponse(String nickname, String introduce, Double star, int reviewCount) {
        this.nickname = nickname;
        this.introduce = introduce;
        this.star = star;
        this.reviewCount = reviewCount;
    }

    public static MyInfoResponse of(String nickname, String introduce, Double star, int reviewCount) {
        return new MyInfoResponse(nickname, introduce, star, reviewCount);
    }
}
