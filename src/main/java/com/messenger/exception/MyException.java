package com.messenger.exception;

public class MyException extends RuntimeException {
    public final ErrorCode errorCode;
    public final Object object;

    public MyException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public MyException(ErrorCode errorCode, Object object) {
        super(errorCode.message);
        this.errorCode = errorCode;
        this.object = object;
    }
}
