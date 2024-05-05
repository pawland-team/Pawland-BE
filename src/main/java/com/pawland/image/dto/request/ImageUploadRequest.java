package com.pawland.image.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadRequest {

    @NotBlank(message = "파일명을 확인해주세요.")
    private String fileName;

    public ImageUploadRequest(String fileName) {
        this.fileName = fileName;
    }
}
