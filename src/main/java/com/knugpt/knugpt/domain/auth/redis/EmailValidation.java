package com.knugpt.knugpt.domain.auth.redis;

import com.knugpt.knugpt.global.constant.Constants;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "emailValidation", timeToLive = 60 * 3)
public class EmailValidation {
    @Id
    private String email;

    private String code;
    private Integer tryCnt;
    private Boolean isValid;

    public static EmailValidation of(String email, String code) {
        return EmailValidation.builder()
                .email(email)
                .code(code)
                .isValid(false)
                .build();
    }

    public EmailValidation addTryCnt() {
        this.tryCnt++;
        return this;
    }

    public boolean isOverLimit() {
        return this.tryCnt >= Constants.EMAIL_VALIDATE_LIMIT;
    }

    public void validationUpdate(Boolean isValid) {
        this.isValid = isValid;
    }

    @Builder
    public EmailValidation(String email, String code, Boolean isValid) {
        this.email = email;
        this.code = code;
        this.isValid = isValid;
        this.tryCnt = 0;
    }
}
