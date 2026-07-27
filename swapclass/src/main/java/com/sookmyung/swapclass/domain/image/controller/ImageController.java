package com.sookmyung.swapclass.domain.image.controller;

import com.sookmyung.swapclass.domain.image.dto.response.ImageUploadResponse;
import com.sookmyung.swapclass.global.response.ApiResponse;
import com.sookmyung.swapclass.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    // 공통 이미지 업로드 (신고 증거, QR 캡처 등)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> upload(
            @RequestParam("image") MultipartFile image) {
        String imageUrl = s3Service.upload(image, "images");
        return ResponseEntity.ok(ApiResponse.success(new ImageUploadResponse(imageUrl)));
    }
}
