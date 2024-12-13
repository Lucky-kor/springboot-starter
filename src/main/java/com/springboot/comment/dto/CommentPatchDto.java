package com.springboot.comment.dto;

import lombok.Getter;


@Getter
public class CommentPatchDto {
    private long commentId;
    private long boardId;

    private String content;

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}
