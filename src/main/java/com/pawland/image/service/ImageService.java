package com.pawland.image.service;

import com.pawland.global.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AwsConfig awsConfig;
    private final S3Presigner s3Presigner;

    public String getPresignedUrl(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("파일명을 확인해주세요.");
        }
        return generatePresignedUrl(filename);
    }

    private String generatePresignedUrl(String filename) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(awsConfig.getS3AccessPoint())
            .key(filename)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(putObjectRequest)
            .build();

        try (S3Presigner presigner = s3Presigner) {
            return presigner.presignPutObject(presignRequest)
                .url()
                .toString();
        }
    }
}