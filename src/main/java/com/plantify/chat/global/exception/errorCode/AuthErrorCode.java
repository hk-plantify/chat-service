package com.plantify.chat.global.exception.errorCode;

import com.plantify.chat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰 형식입니다."),
    TOKEN_CLAIMS_EMPTY(HttpStatus.BAD_REQUEST, "토큰의 클레임이 비어 있습니다."),
    ACCESS_TOKEN_NULL(HttpStatus.BAD_REQUEST, "액세스 토큰이 비어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
