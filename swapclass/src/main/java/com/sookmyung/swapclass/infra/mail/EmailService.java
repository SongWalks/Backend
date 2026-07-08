package com.sookmyung.swapclass.infra.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAuthCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[수강구조대] 이메일 인증코드");
        message.setText("인증코드: " + code + "\n5분 이내에 입력해 주세요.");
        mailSender.send(message);
    }
}
