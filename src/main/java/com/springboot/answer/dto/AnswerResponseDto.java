package com.springboot.answer.dto;


import com.springboot.board.entity.Board;
import com.springboot.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerResponseDto {
    private long answerId;

    @Setter(AccessLevel.NONE)
    private long memberId;
    private long boardId;
    private String content;
    private LocalDateTime createdAt;

    public void setMember(Member member){
        this.memberId = member.getMemberId();
    }
    public void setBoard(Board board){
        this.boardId = board.getBoardId();
    }

}
