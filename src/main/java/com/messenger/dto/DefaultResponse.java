package com.messenger.dto;

import com.messenger.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DefaultResponse<T> {

    private final HttpStatus resultCode;
    private final int statusCode;
    private final String message;
    private final T data;

    private DefaultResponse(HttpStatus resultCode, String message, T data) {
        this.resultCode = resultCode;
        this.statusCode = resultCode.value();
        this.message = message;
        this.data = data;
    }

    // 정적 팩토리 메서드
    public static <T> DefaultResponse<T> of(HttpStatus resultCode, String message, T data) {
        return new DefaultResponse<>(resultCode, message, data);
    }

    public static <T> DefaultResponse<T> ofFail(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    public static <T> DefaultResponse<T> ofFail(ErrorCode errorCode) {
        return of(errorCode.httpStatusCode, errorCode.message, null);
    }

    public static <T> DefaultResponse<T> ofSuccess() {
        return of(HttpStatus.OK, "success", null);
    }

    public static <T> DefaultResponse<T> ofSuccess(T data) {
        return of(HttpStatus.OK, "success", data);
    }
}
