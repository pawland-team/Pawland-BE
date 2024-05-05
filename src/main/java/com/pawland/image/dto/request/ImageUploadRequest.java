package com.pawland.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "Presigned URL 발급 요청")
public class ImageUploadRequest {

    @NotBlank(message = "파일명을 확인해주세요.")
    private String fileName;

    public ImageUploadRequest(String fileName) {
        this.fileName = fileName;
    }
}
