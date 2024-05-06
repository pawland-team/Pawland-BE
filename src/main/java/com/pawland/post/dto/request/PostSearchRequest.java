package com.pawland.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PostSearchRequest {
    private int page;
    private String content;
    private String region;
}
