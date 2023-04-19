package com.messenger.dto;

import com.messenger.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DefaultResponse {

    private final HttpStatus resultCode;
    private final int statusCode;
    private final String message;

    private DefaultResponse(HttpStatus resultCode, String message) {
        this.resultCode = resultCode;
        this.statusCode = resultCode.value();
        this.message = message;
    }

    public static DefaultResponse of(HttpStatus resultCode, String message) {
        return new DefaultResponse(resultCode, message);
    }

    public static DefaultResponse ofFail(String message) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static DefaultResponse ofFail(ErrorCode errorCode) {
        return of(errorCode.httpStatusCode, errorCode.message);
    }

    public static DefaultResponse ofSuccess() {
        return of(HttpStatus.OK, "success");
    }

}
