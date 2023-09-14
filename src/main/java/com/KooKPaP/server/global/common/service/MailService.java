package com.KooKPaP.server.global.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;

    public void sendEmail(String address, String title, String text) throws Throwable {
        // HTML 형식으로 메세지를 보냄
        // address: email, title 메세지 제목, text 내용
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setTo(address);
        helper.setSubject(title);
        helper.setText(text, true);

        emailSender.send(message);
    }
}