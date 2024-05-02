package com.pawland.global.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DefaultImage {

    DEFAULT_PROFILE_IMAGE("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/myKey4"),
    DEFAULT_POST_IMAGE("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/myKey4");

    private final String imageUrl;

    public String value() {
        return imageUrl;
    }
}
