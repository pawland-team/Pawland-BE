package com.pawland.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    KAKAO("카카오"),
    GOOGLE("구글"),
    NAVER("네이버"),
    NORMAL("일반");

    private final String type;

    public String value() {
        return type;
    }

    public static LoginType fromString(String input) {
        if (input == null || input.isBlank()) {
            return LoginType.NORMAL;
        }
        return Arrays.stream(LoginType.values()).
            filter(region -> region.value().equals(input))
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("지역 값을 확인해주세요."));  // TODO: 커스텀 예외로 만들어도 될듯
    }
}
