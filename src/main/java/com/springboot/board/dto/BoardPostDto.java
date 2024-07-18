package com.springboot.board.dto;

import com.springboot.board.entity.Board;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
public class BoardPostDto {
//    @Positive
//    private long memberId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Board.VisibilityStatus visibilityStatus;

}
