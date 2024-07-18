package com.springboot.comment.dto;

import com.springboot.board.entity.Board;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
public class CommentPostDto {
    private long boardId;

    @NotBlank
    private String content;

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
