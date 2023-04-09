package com.messenger.exception;

public class MyException extends RuntimeException {
    public final ErrorCode errorCode;

    public MyException(ErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
    }
}
