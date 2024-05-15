package com.pawland.image.controller;

import com.pawland.image.dto.request.ImageUploadRequest;
import com.pawland.image.dto.response.ImageUploadResponse;
import com.pawland.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
@SecurityRequirement(name = "jwt-cookie")
@Tag(name = "ImageController", description = "이미지 관련 컨트롤러 입니다.")
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "이미지 저장용 Presigned URL 발급", description = "요청한 이미지 파일명으로 Presigned URL을 발급합니다.")
    @ApiResponse(responseCode = "201", description = "Presigned URL 발급 성공")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(@Valid @RequestBody ImageUploadRequest request) {
        return ResponseEntity
            .status(CREATED)
            .body(new ImageUploadResponse(imageService.getPresignedUrl(request.getFileName())));
    }
}
