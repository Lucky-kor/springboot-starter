package com.springboot.comment.dto;

import lombok.Getter;


@Getter
public class CommentPatchDto {
    private long commentId;

    private String content;

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}
