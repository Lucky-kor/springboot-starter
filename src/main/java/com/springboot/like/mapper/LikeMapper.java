package com.springboot.like.mapper;

import com.springboot.board.entity.Board;
import com.springboot.like.dto.LikePostDto;
import com.springboot.like.entity.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    default Like likePostDtoToLike(LikePostDto likePostDto){
        Like like = new Like();
        Board board = new Board();
        board.setBoardId(likePostDto.getBoardId());
        like.setBoard(board);

        return like;
    }
}
