package com.knugpt.knugpt.global.exception;


import com.knugpt.knugpt.global.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    // Convertor 에서 바인딩 실패시 발생하는 예외
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseDto<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch HttpMessageNotReadableException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(new CommonException(ErrorCode.BAD_REQUEST_JSON));
    }

    // 지원되지 않는 HTTP 메소드를 사용할 때 발생하는 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseDto<?> handleNoPageFoundException(Exception e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch NoHandlerFoundException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(new CommonException(ErrorCode.NOT_FOUND_END_POINT));
    }


    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseDto<?> handleArgumentNotValidException(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch MethodArgumentTypeMismatchException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(e);
    }

    @ExceptionHandler(value = {JsonWebTokenException.class})
    public ResponseDto<?> handleJsonWebTokenException(JsonWebTokenException e, HttpServletRequest req) {
        String requestMethod = req.getMethod();
        String requestURI = req.getRequestURI();

        log.error("GlobalExceptionHandler catch JsonWebTokenException By User(id:{}) When [{}] {} In [{}] At {} : {}",
                getUserName(),
                requestMethod,
                requestURI,
                getMethodName(e),
                getLineNumber(e),
                e.getMessage());

        return ResponseDto.fail(e);
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
