package com.messenger.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyControllerAdvice {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<String> myExceptionHandler(MyException e) {
        log.error("MyException = {} {}", e.errorCode.httpStatusCode, e.errorCode.message);
        return new ResponseEntity<>(e.errorCode.message, e.errorCode.httpStatusCode);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        log.error("Exception = {}", e.getMessage());
        e.printStackTrace();
        return e.getMessage();
    }
}
