package com.messenger.exception;

import com.messenger.dto.DefaultResponse;
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
    public ResponseEntity<DefaultResponse> myExceptionHandler(MyException e) {
        log.debug("MyException = {} {}", e.errorCode.httpStatusCode, e.errorCode.message);
        return new ResponseEntity<>(DefaultResponse.ofFail(e.errorCode), e.errorCode.httpStatusCode);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public DefaultResponse exceptionHandler(Exception e) {
        log.error("Exception = {}", e.getMessage());
        e.printStackTrace();
        return DefaultResponse.ofFail(e.getMessage());
    }
}
