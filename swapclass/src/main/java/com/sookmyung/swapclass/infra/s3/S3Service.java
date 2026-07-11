package com.sookmyung.swapclass.infra.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    // TODO: S3 버킷 생성 후 실제 S3 업로드로 교체 예정
    private static final String LOCAL_UPLOAD_DIR = "uploads/";

    // MultipartFile 업로드 (임시 로컬 저장)
    public String upload(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(LOCAL_UPLOAD_DIR + folder);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            // 임시 URL 반환
            return "http://localhost:8080/uploads/" + folder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // byte[] 업로드 (QR 이미지용, 임시 로컬 저장)
    public String uploadBytes(byte[] bytes, String folder, String fileName, String contentType) {
        try {
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;
            Path uploadPath = Paths.get(LOCAL_UPLOAD_DIR + folder);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.write(filePath, bytes);
            return "http://localhost:8080/uploads/" + folder + "/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
