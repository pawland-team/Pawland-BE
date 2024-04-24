package com.pawland.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    KAKAO("카카오"),
    GOOGLE("구글"),
    NAVER("네이버"),
    NORMAL("일반");

    private final String type;
}
