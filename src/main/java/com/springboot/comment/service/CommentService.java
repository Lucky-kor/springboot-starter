package com.springboot.comment.service;

import com.springboot.board.entity.Board;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.service.BoardService;
import com.springboot.comment.entity.Comment;
import com.springboot.comment.repository.CommentRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
    }

    public Comment createComment (Comment comment) {
        // 이미 등록되어 있는지 아닌지 확인
        verifyComment(comment.getBoard().getBoardId());
       Optional<Board> findBoard = boardRepository.findById(comment.getBoard().getBoardId());
       Board board = findBoard.orElseThrow(()->new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
       board.setQuestionStatus(Board.QuestionStatus.QUESTION_ANSWERED);

        // 답변 등록 시 등록 날짜 생성. (이미 엔티티에 초기화 되어 있음.)
        // 질문이 비밀글이면 답변 비밀글.... 같이 가야 함.
        // 비공개 일때만
        if (board.getVisibilityStatus().equals(Board.VisibilityStatus.SECRET)) {
            comment.setVisibilityStatus(Comment.VisibilityStatus.SECRET);
        }

        return commentRepository.save(comment);
    }

    public Comment updateComment (Comment comment) {
        // 있는 코멘트 데리고 옴.
        Comment findComment = findVerifiedComment(comment.getCommentId());
        Optional.ofNullable(comment.getContent())
                .ifPresent(content -> findComment.setContent(content));
        // 답변 수정 시 수정 날짜 생성.
        findComment.setModifiedAt(LocalDateTime.now());

        return commentRepository.save(findComment);
    }

    public Comment findComment (long commentId) {
        Comment findComment = findVerifiedComment(commentId);
        return findComment;
    }

    public void deleteComment (long commentId) {
        // 삭제 시 테이블에서 Row 가 완전히 삭제될 수 있도록 함.
        Comment findComment = findVerifiedComment(commentId);
        commentRepository.delete(findComment);
    }

    public void verifyComment (long boardId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board board = optionalBoard.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        if(board.getComment() != null) {
            throw new BusinessLogicException(ExceptionCode.COMMENT_EXISTS);
        }
    }

    // comment의 id 가 있는 Id인지 확인.
    public Comment findVerifiedComment (long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Comment findComment = optionalComment.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND)
        );
        return findComment;
    }

}
