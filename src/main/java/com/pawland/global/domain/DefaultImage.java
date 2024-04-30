package com.pawland.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefaultImage {

    Profile("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/myKey4");

    private final String imageUrl;
}
