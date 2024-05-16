package com.pawland.global.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public enum DefaultImage {

    DEFAULT_PROFILE_IMAGE_1("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/pawlandRP_1.png"),
    DEFAULT_PROFILE_IMAGE_2("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/pawlandRP_2.png"),
    DEFAULT_PROFILE_IMAGE_3("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/pawlandRP_3.png"),
    DEFAULT_PROFILE_IMAGE_4("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/pawlandRP_4.png"),
    DEFAULT_PROFILE_IMAGE_5("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/pawlandRP_5.png"),
    DEFAULT_POST_IMAGE("https://midcon-bucket.s3.ap-northeast-2.amazonaws.com/myKey4");

    private final String imageUrl;

    public String value() {
        return imageUrl;
    }

    public static String getRandomProfileImage() {
        List<DefaultImage> defaultProfileImageList = List.of(
            DEFAULT_PROFILE_IMAGE_1,
            DEFAULT_PROFILE_IMAGE_2,
            DEFAULT_PROFILE_IMAGE_3,
            DEFAULT_PROFILE_IMAGE_4,
            DEFAULT_PROFILE_IMAGE_5
        );
        Random random = new Random();
        int index = random.nextInt(defaultProfileImageList.size());
        return defaultProfileImageList.get(index).value();
    }
}
