package com.sookmyung.swapclass.infra.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QrService {

    private static final int QR_SIZE = 300;

    // QR 이미지 바이트 배열 생성
    public byte[] generateQrImage(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();

        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR 이미지 생성 실패", e);
        }
    }

    // 이미지에서 QR 코드 디코딩
    public String decodeQrImage(byte[] imageBytes) {
        try {
            java.io.InputStream inputStream = new java.io.ByteArrayInputStream(imageBytes);
            java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(inputStream);

            com.google.zxing.client.j2se.BufferedImageLuminanceSource source =
                    new com.google.zxing.client.j2se.BufferedImageLuminanceSource(bufferedImage);
            com.google.zxing.common.HybridBinarizer binarizer =
                    new com.google.zxing.common.HybridBinarizer(source);
            com.google.zxing.BinaryBitmap bitmap =
                    new com.google.zxing.BinaryBitmap(binarizer);

            com.google.zxing.MultiFormatReader reader = new com.google.zxing.MultiFormatReader();
            com.google.zxing.Result result = reader.decode(bitmap);
            return result.getText();

        } catch (Exception e) {
            return null; // 디코딩 실패 시 null 반환
        }
    }
}
