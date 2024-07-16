package com.springboot.comment.controller;

import com.springboot.board.entity.Board;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.service.BoardService;
import com.springboot.comment.dto.CommentPatchDto;
import com.springboot.comment.dto.CommentPostDto;
import com.springboot.comment.dto.CommentResponseDto;
import com.springboot.comment.entity.Comment;
import com.springboot.comment.mapper.CommentMapper;
import com.springboot.comment.repository.CommentRepository;
import com.springboot.comment.service.CommentService;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping("/v2/comments")
@Validated
public class CommentController {
    private final static String COMMENT_DEFAULT_URL = "/v2/comments";

    private final CommentService commentService;
    private final CommentMapper mapper;

    public CommentController(CommentService commentService, CommentMapper mapper) {
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postComment (@Valid @RequestBody CommentPostDto commentPostDto) {
        Comment comment = mapper.commentPostDtoToComment(commentPostDto);
        Board board = new Board();
        board.setBoardId(commentPostDto.getBoardId());
        comment.setBoard(board);

        Comment createdComment = commentService.createComment(comment);
        URI location = UriCreator.createUri(COMMENT_DEFAULT_URL, createdComment.getCommentId());
        return ResponseEntity.created(location).build();

    }

    @PatchMapping("{comment-id}")
    public ResponseEntity patchComment (@PathVariable("comment-id") @Positive long commentId,
                                        @Valid @RequestBody CommentPatchDto commentPatchDto) {
        commentPatchDto.setCommentId(commentId);
        Comment comment = commentService.updateComment(mapper.commentPatchDtoToComment(commentPatchDto));
        CommentResponseDto commentResponseDto = mapper.commentToCommentResponseDto(comment);
        return new ResponseEntity<>(
                new SingleResponseDto<>(commentResponseDto), HttpStatus.OK
        );
    }
    @GetMapping("{comment-id}")
    public ResponseEntity getComment (@PathVariable("comment-id") @Positive long commentId) {
        Comment comment = commentService.findComment(commentId);
        CommentResponseDto commentResponseDto = mapper.commentToCommentResponseDto(comment);

        return new ResponseEntity<>(
                new SingleResponseDto<>(commentResponseDto), HttpStatus.OK
        );
    }

    @DeleteMapping("{comment-id}")
    public ResponseEntity deleteComment (@PathVariable("comment-id") @Positive long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}

