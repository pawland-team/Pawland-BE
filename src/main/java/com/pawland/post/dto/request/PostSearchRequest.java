package com.pawland.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchRequest {
    @NotBlank
    private int page;
    private String content;
    private String region;
    private String orderBy;
}
