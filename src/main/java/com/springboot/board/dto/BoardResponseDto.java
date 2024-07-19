package com.springboot.board.dto;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entity.Answer;
import com.springboot.board.entity.Board;
import com.springboot.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
public class BoardResponseDto {
    private long boardId;

    @Setter(AccessLevel.NONE)
    private long memberId;
    private Board.BoardStatus boardStatus;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer viewCount;
    private Integer likeCount;

    private AnswerResponseDto answerResponseDto;

    public void setMember(Member member){
        this.memberId = member.getMemberId();
    }


}
