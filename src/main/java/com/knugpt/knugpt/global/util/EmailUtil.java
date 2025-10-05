package com.knugpt.knugpt.global.util;

import com.knugpt.knugpt.global.exception.CommonException;
import com.knugpt.knugpt.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendEmailValidationMail(String email, String validateCode){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[KNU-GPT] 이메일 인증 코드입니다.");
            mimeMessageHelper.setText(setContext("code", validateCode), true);

            sendMail(mimeMessage);

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
