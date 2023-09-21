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
    private final String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int codeLength = 6;

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

    public String sendAuthCode(String address)throws Throwable{
        // 인증 코드 랜덤 생성 & 전송
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            code.append(alphaNumeric.charAt(random.nextInt(alphaNumeric.length())));
        }

        String title = "[KookPap] 이메일 인증 코드";
        String text = "";
        text += "<h3>" + "KookPap 이메일 인증 코드입니다." + "</h3>";
        text += "<h1>" + code.toString() + "</h1>";
        text += "<h3>" + "인증 코드는 30분간 유효합니다." + "</h3>";

        sendEmail(address, title, text);
        return code.toString();
    }
}