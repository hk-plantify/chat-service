package com.plantify.chat.global.exception;

import com.plantify.chat.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<Void> handleApplicationException(ApplicationException e) {
        return ApiResponse.fail(e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러");
    }
}