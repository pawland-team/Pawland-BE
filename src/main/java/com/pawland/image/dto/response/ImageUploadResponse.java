package com.pawland.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "Presigned URL 발급 응답")
public class ImageUploadResponse {

    private String presignedUrl;

    public ImageUploadResponse(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
