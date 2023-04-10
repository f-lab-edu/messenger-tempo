package com.messenger.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND_MEMBER("사용자를 찾을 수 없음", HttpStatus.NOT_FOUND),
    NOT_MATCH_PASSWORD("비밀번호가 일치하지 않음", HttpStatus.UNAUTHORIZED),
    NOT_MODIFIED("변경사항 없음", HttpStatus.NOT_MODIFIED),
    FAIL_UPDATE_MEMBER("업데이트 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    FAIL_SIGNUP("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_EXIST_ID("이미 존재하는 아이디", HttpStatus.CONFLICT),
    ALREADY_LOGIN("이미 로그인 중인 사용자", HttpStatus.NOT_MODIFIED),
    UNAUTHORIZED("권한이 없음", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("서버 내부 에러", HttpStatus.INTERNAL_SERVER_ERROR),
    OK("정상", HttpStatus.OK),


    FAIL_SAVE_CHAT("채팅 메시지 저장 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    FAIL_DELETE_CHAT("잘못된 요청으로 메시지를 삭제할 수 없음", HttpStatus.BAD_REQUEST),
    NOT_FOUND_CHAT("채팅 메시지를 찾을 수 없음", HttpStatus.NOT_FOUND),
    ;

    public final String message;
    public final HttpStatus httpStatusCode;

    ErrorCode(String message, HttpStatus httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
