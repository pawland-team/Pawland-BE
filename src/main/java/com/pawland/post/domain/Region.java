package com.pawland.post.domain;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Region {

    SEOUL("서울"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    GWANGJU("광주"),
    DAEGEON("대전"),
    ULSAN("울산"),
    SEJONG("세종"),
    GYEONGGI("경기"),
    GANGWOND("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    JEONBUK("전북"),
    JEONNAM("전남"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    JEJU("제주"),
    FOREIGN("해외");

    private final String region;

    public String value() {
        return region;
    }

    public static Region fromString(String input) {
        if (input == null || input.isBlank()) {
            return Region.SEOUL;
        }
        return Arrays.stream(Region.values()).
            filter(region -> region.value().equals(input))
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("지역 값을 확인해주세요."));  // TODO: 커스텀 예외로 만들어도 될듯
    }
}
