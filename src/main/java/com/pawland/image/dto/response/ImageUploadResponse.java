package com.pawland.image.dto.response;

import lombok.Getter;

@Getter
public class ImageUploadResponse {

    private String presignedUrl;

    public ImageUploadResponse(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}
