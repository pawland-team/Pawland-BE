package com.pawland.image.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @DisplayName("정상적인 파일명일 시 Presigned Url을 생성한다.")
    @Test
    void getPresignedUrl1() {
        // given
        String filename = "test";

        // when
        String result = imageService.getPresignedUrl(filename);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotBlank();
    }

    @DisplayName("파일명이 Null 이거나 공백이면 예외를 던진다.")
    @Test
    void getPresignedUrl2() {
        // given
        String filename1 = "";
        String filename2 = "  ";
        String filename3 = null;

        // expected
        Assertions.assertThatThrownBy(() -> imageService.getPresignedUrl(filename1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일명을 확인해주세요.");
        Assertions.assertThatThrownBy(() -> imageService.getPresignedUrl(filename2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일명을 확인해주세요.");
        Assertions.assertThatThrownBy(() -> imageService.getPresignedUrl(filename3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일명을 확인해주세요.");
    }
}
