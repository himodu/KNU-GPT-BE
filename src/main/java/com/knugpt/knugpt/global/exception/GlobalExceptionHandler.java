package com.knugpt.knugpt.global.exception;


import com.knugpt.knugpt.global.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.knugpt.knugpt.global.exception.ExceptionUtil.*;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    // 개발자가 직접 정의한 예외
    @ExceptionHandler(value = {CommonException.class})
    public ResponseDto<?> handleApiException(CommonException e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch CommonException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(e);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseDto<?> handleBadCredentialException(BadCredentialsException e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch BadCredentialsException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(new CommonException(ErrorCode.INVALID_PASSWORD));
    }

    // 서버, DB 예외
    @ExceptionHandler(value = {Exception.class})
    public ResponseDto<?> handleException(Exception e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch {} By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getSimpleName(e),
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal =  authentication.getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            return "None";
        }
    }
}
