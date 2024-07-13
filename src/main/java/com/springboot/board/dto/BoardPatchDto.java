package com.springboot.board.dto;

import com.springboot.board.entity.Board;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class BoardPatchDto {
    private long boardId;
    private long viewMemberId;

    private String title;
    private String content;
    private Board.VisibilityStatus visibilityStatus;

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
