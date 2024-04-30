package com.pawland.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegionTest {

    @DisplayName("입력 값에 해당하는 Region을 가져온다.")
    @Test
    void fromString1() {
        // given
        Region result = Region.fromString("부산");

        // expected
        assertThat(result).isEqualTo(Region.BUSAN);
    }

    @DisplayName("설정 지역 이외의 지역 입력 시 예외를 던진다.")
    @Test
    void fromString2() {
        // expected
        assertThatThrownBy(() -> Region.fromString("나는짱"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지역 값을 확인해주세요.");
    }
}