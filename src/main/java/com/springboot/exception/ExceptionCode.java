package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_MEMBER_STATUS(400, "Invalid member status"),
    BOARD_NOT_FOUND(404, "Board not found"),
    ANSWER_NOT_FOUND(404, "Answer not found"),
    NOT_YOUR_BOARD(409, "NOT_YOUR_BOARD"),
    NOT_YOUR_ANSWER(409, "NOT_YOUR_ANSWER"),
    LIKE_NOT_FOUND(404,"LIKE_NOT_FOUND");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
