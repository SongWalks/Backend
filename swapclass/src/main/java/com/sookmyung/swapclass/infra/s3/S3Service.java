package com.sookmyung.swapclass.infra.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile 업로드
    public String upload(MultipartFile file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
        return "https://" + bucket + ".s3.amazonaws.com/" + fileName;
    }

    // byte[] 업로드 (QR 이미지용)
    public String uploadBytes(byte[] bytes, String folder, String fileName, String contentType) {
        String uniqueFileName = folder + "/" + UUID.randomUUID() + "_" + fileName;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueFileName)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return "https://" + bucket + ".s3.amazonaws.com/" + uniqueFileName;
    }
}