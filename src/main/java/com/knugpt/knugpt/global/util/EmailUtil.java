package com.knugpt.knugpt.global.util;

import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;

    public void sendEmailValidationMail(String email, String validateCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[KNU-GPT] 이메일 인증 코드입니다.");
            mimeMessageHelper.setText(setContext("emailValidation", validateCode), true);
            mimeMessageHelper.setFrom(new InternetAddress(username, "KNU-GPT", "UTF-8"));

            sendMail(mimeMessage);

        } catch (UnsupportedEncodingException e) {
            log.info("이메일 전송 실패");
            throw new CommonException(ErrorCode.EMAIL_SEND_ERROR);

        } catch (MessagingException e){
            log.info("이메일 전송 실패");
            throw new CommonException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    public void sendMail(MimeMessage mimeMessage) {
        mailSender.send(mimeMessage);
        log.info("이메일 전송 성공");
    }

    public String setContext(String type, String code){
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}
