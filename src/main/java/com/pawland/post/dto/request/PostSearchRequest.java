package com.pawland.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchRequest {
    @NotBlank
    private int page;
    @NotBlank
    private int size;
    private String content;
    private List<String> region;
    private String orderBy;
}
