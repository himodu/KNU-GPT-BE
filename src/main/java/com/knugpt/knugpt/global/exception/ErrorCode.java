package com.knugpt.knugpt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Not Found Error
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API 엔드포인트입니다."),
    NOT_FOUND_USER(40401, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NOT_FOUND_CHAT_ROOM(40402, HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),

    // Invalid Argument Error
    INVALID_HEADER_ERROR(40000, HttpStatus.BAD_REQUEST, "유효하지 않은 헤더입니다."),
    MISSING_REQUEST_PARAMETER(40000, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_PARAMETER_FORMAT(40001, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자 형식입니다."),
    INVALID_ARGUMENT(40002, HttpStatus.BAD_REQUEST, "유효하지 않은 인자입니다."),
    ALREADY_HAVE_LIKE(40003, HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    BAD_REQUEST_JSON(40004, HttpStatus.BAD_REQUEST, "json 형식이 맞지 않습니다."),

    // Access Denied Error
    ACCESS_DENIED(40300, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    TOKEN_EXPIRED(40301, HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN_FORMAT(40302, HttpStatus.FORBIDDEN, "토큰 형식이 맞지 않습니다. 올바른 토큰 형식 'Authorization' : 'Bearer {token}')"),
    POST_EDIT_FORBIDDEN(40303, HttpStatus.FORBIDDEN, "게시물을 수정할 권한이 없습니다."),
    EMAIL_NOT_VALID(40304, HttpStatus.FORBIDDEN, "이메일이 인증되지 않았습니다."),
    EMAIL_CODE_NOT_VALID(40305, HttpStatus.FORBIDDEN, "이메일 인증코드가 일치하지 않습니다."),
    EMAIL_VALIDATE_LIMIT_OVER(40306, HttpStatus.FORBIDDEN, "이메일 인증 요청 가능 횟수 5회를 초과했습니다."),
    INVALID_PASSWORD(40307, HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    EMAIL_SEND_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 오류입니다."),
    INTERNAL_DATA_ERROR(50002, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 데이터 에러입니다."),
    LLM_SERVER_ERROR(50003, HttpStatus.INTERNAL_SERVER_ERROR, "LLM 서버 오류입니다.")
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
