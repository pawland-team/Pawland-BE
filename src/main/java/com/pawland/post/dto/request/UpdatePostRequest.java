package com.pawland.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePostRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private String content;

    private String thumbnail;

    private String region;
}
