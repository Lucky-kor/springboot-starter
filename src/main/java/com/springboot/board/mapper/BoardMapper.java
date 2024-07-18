package com.springboot.board.mapper;

import com.springboot.answer.dto.AnswerResponseDto;
import com.springboot.answer.entity.Answer;
import com.springboot.answer.mapper.AnswerMapper;
import com.springboot.board.dto.BoardPatchDto;
import com.springboot.board.dto.BoardPostDto;
import com.springboot.board.dto.BoardResponseDto;
import com.springboot.board.entity.Board;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    Board boardPatchDtoToBoard(BoardPatchDto boardPatchDto);
    default Board boardPostDtoToBoard(BoardPostDto boardPostDto){
        Board board = new Board();

        board.setTitle(boardPostDto.getTitle());
        board.setContent(boardPostDto.getContent());
        board.setBoardSecret(boardPostDto.getBoardSecret());

        return board;
    }
    default BoardResponseDto boardToBoardResponseDto(Board board){
        BoardResponseDto boardResponseDto = new BoardResponseDto();

        boardResponseDto.setBoardId(board.getBoardId());
        boardResponseDto.setBoardStatus(board.getBoardStatus());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setContent(board.getContent());
        boardResponseDto.setCreatedAt(board.getCreatedAt());
        boardResponseDto.setMember(board.getMember());

        AnswerResponseDto answerResponseDto = new AnswerResponseDto();
        if(board.getAnswer() == null){
            answerResponseDto = null;
        }else {
            answerResponseDto.setAnswerId(board.getAnswer().getAnswerId());
            answerResponseDto.setMember(board.getMember());
            answerResponseDto.setContent(board.getContent());
            answerResponseDto.setCreatedAt(board.getCreatedAt());
            answerResponseDto.setBoardId(board.getBoardId());
            boardResponseDto.setAnswerResponseDto(answerResponseDto);
        }

        return boardResponseDto;
    }
    List<BoardResponseDto> boardsToBoardResponseDtos(List<Board> boards);
}
