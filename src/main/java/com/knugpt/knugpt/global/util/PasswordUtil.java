package com.knugpt.knugpt.global.util;

public class PasswordUtil {
    public static String generateAuthCode(Integer length) {
        StringBuilder authCode = new StringBuilder();

        // 숫자로만 구성된 인증 코드 생성
        for (int i = 0; i < length; i++) {
            authCode.append((int) (Math.random() * 10));
        }

        return authCode.toString();
    }
}
