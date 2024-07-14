package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_EXISTS("Member already exists"),
    MEMBER_NOT_FOUND("Member not found"),
    INVALID_VISIBILITY_STATUS("Choose either PUBLIC or SECRET"),
    QUESTION_NOT_FOUND("Question not found"),
    NO_PERMISSION_TO_VIEW("No permission to view"),
    NO_PERMISSION_TO_DELETE("No permission to delete"),
    NO_PERMISSION_TO_EDIT("No permission to edit"),
    QUESTION_DELETED("Question deleted"),
    QUESTION_ALREADY_DELETED("Question already deleted. Unable to view"),
    QUESTION_CANNOT_BE_EDITED("Question already has a comment.Cannot be edited");

    @Getter
    private final String message;

    ExceptionCode(String message) {
        this.message = message;
    }
}
