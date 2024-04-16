package com.pawland.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {

    KAKAO("카카오"),
    GOOGLE("구글"),
    NORMAL("일반");

    private final String type;
}
