package com.springboot.comment.dto;

import com.springboot.board.entity.Board;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
public class CommentPostDto {
    @Positive
    private long boardId;

    @Positive
    private long memberId;

    @NotBlank
    private String content;

}
