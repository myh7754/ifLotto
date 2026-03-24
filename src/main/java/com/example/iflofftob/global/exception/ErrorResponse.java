package com.example.iflofftob.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 에러 응답의 표준 포맷.
 * 모든 예외는 이 형태로 직렬화되어 클라이언트에 반환된다.
 */
@Getter
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;

    public static ErrorResponse of(int status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(int status, String message, List<String> errors) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
