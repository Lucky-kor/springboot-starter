package com.springboot.board.dto;

import com.springboot.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardPatchDto {
    private long boardId;
    private String title;
    private String content;

    public void setBoardId(long boardId){
        this.boardId = boardId;
    }
}
