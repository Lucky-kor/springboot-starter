package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    COMMENT_NOT_FOUND(404, "Comment not found"),
    COMMENT_EXISTS(409, "Comment exists"),
    VIEW_NOT_FOUND(404, "View not found"),
    NO_AUTHORITY (403, "No Authority"),
    DO_NOT_HAVE_POST_PERMISSION (403, "Do not have permission to create this post"),
    DIFFERENT_USER (403, "Different User"),
    DO_NOT_HAVE_GET_PERMISSION (403, "Do not have permission to look up this post"),
    DO_NOT_HAVE_DELETE_PERMISSION (403, "Do not have permission to delete this post"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    BOARD_NOT_FOUND(404, "Board not found"),
    CANNOT_CHANGE_BOARD(403, "Board can not change"),
    CANNOT_DELETE_BOARD(403, "Board can not delete"),
    LIKE_EXISTS(404, "like exists");
    // enum 만들 때.
    // 필요한 필드를 주입을 해서
    // 생성자를 만들어 주어야 함.

    @Getter
    private int status;

    @Getter
    private String message;
    // status 가 코드 임.
    //
    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
