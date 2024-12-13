package com.springboot.board.mapper;

import com.springboot.board.dto.*;
import com.springboot.board.entity.Board;
import com.springboot.board.entity.Like;
import com.springboot.board.entity.View;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    Board boardPostDtoToBoard (BoardPostDto boardPostDto);
    Board boardPatchDtoTOBoard (BoardPatchDto boardPatchDto);

    @Mapping(source = "member.memberId", target = "memberId")
    @Mapping(source = "comment.content", target = "boardComment.content")
    @Mapping(source = "comment.member.memberId", target = "boardComment.memberId")
    BoardResponseDto boardToBoardResponseDto (Board board);
    List<BoardResponseDto> boardsToBoardResponseDtos (List<Board> boards);

    @Mapping(source = "memberId", target = "member.memberId")
    Board BoardGetDtoToBoard (BoardGetDto viewGetDto);

}
