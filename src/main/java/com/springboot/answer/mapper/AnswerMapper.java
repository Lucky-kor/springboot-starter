package com.springboot.answer.mapper;


import com.springboot.answer.dto.AnswerPatchDto;
import com.springboot.answer.dto.AnswerPostDto;
import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entity.Answer;
import com.springboot.board.entity.Board;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    default Answer answerPostDtoToAnswer(AnswerPostDto answerPostDto){
        Answer answer = new Answer();
        Board board = new Board();
        board.setBoardId(answerPostDto.getBoardId());
        answer.setBoard(board);
        answer.setContent(answerPostDto.getContent());
        return answer;
    }
    Answer answerPatchDtoToAnswer(AnswerPatchDto answerPatchDto);
    AnswerResponseDto answerToAnswerResponseDto(Answer answer);
    List<AnswerResponseDto> answersToAnswerResponseDtos(List<Answer> answers);
}
