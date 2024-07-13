package com.springboot.comment.mapper;

import com.springboot.comment.dto.CommentPatchDto;
import com.springboot.comment.dto.CommentPostDto;
import com.springboot.comment.dto.CommentResponseDto;
import com.springboot.comment.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "boardId",target = "board.boardId")
    @Mapping(source = "memberId", target = "member.memberId")
    Comment commentPostDtoToComment (CommentPostDto commentPostDto);
    Comment commentPatchDtoToComment (CommentPatchDto commentPatchDto);


    @Mapping(source = "member.memberId", target = "memberId")
    CommentResponseDto commentToCommentResponseDto (Comment comment);
}
