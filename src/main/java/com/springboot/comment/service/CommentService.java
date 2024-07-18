package com.springboot.comment.service;

import com.springboot.board.entity.Board;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.service.BoardService;
import com.springboot.comment.entity.Comment;
import com.springboot.comment.repository.CommentRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, MemberService memberService) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.memberService = memberService;
    }

    public Comment createComment (Comment comment, Authentication authentication) {
            Optional<Board> findBoard = boardRepository.findById(comment.getBoard().getBoardId());
            verifyComment(findBoard.get().getBoardId());
            Board board = findBoard.orElseThrow(()->new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
            board.setQuestionStatus(Board.QuestionStatus.QUESTION_ANSWERED);
            // 객체 간의 entity 를 영속성을 넣을 때에는 무조건. set을 해서 상태를 넣어주어야 함.
            String email = (String) authentication.getPrincipal();
            Member member = memberService.findVerifiedMember(email);
            comment.setMember(member);
            comment.setBoard(board);

            // 답변 등록 시 등록 날짜 생성. (이미 엔티티에 초기화 되어 있음.)
            // 질문이 비밀글이면 답변 비밀글.... 같이 가야 함.
            // 비공개 일때만
            if (board.getVisibilityStatus().equals(Board.VisibilityStatus.SECRET)) {
                comment.setVisibilityStatus(Comment.VisibilityStatus.SECRET);
            }
            return commentRepository.save(comment);

    }

    public Comment updateComment (Comment comment, Authentication authentication) {
        //
        String email = (String) authentication.getPrincipal();
        Member member = memberService.findVerifiedMember(email);
        Comment findComment = findVerifiedComment(comment.getCommentId());
        // admin 이고, comment 를 쓴 사람이면.
        if( isCheckCommentOwner(authentication, member)) {
            // 있는 코멘트 데리고 옴.
//            verifyComment(comment.getBoard().getBoardId());
            Optional.ofNullable(comment.getContent())
                    .ifPresent(content -> findComment.setContent(content));
            // 답변 수정 시 수정 날짜 생성.
            findComment.setModifiedAt(LocalDateTime.now());

            return commentRepository.save(findComment);

        } else {
            throw new BusinessLogicException(ExceptionCode.DIFFERENT_USER);
        }
    }


    public Comment findComment (long commentId) {
        Comment findComment = findVerifiedComment(commentId);
        return findComment;
    }

    public void deleteComment (long commentId,Authentication authentication) {
        Comment findComment = findVerifiedComment(commentId);
            // 삭제 시 테이블에서 Row 가 완전히 삭제될 수 있도록 함.
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

    // comment의 id 가 있는 Id인지 확인. update 할 때 필요.
    public Comment findVerifiedComment (long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Comment findComment = optionalComment.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND)
        );
        return findComment;
    }

    public boolean verifyAdmin (Authentication authentication) {
       boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
       if (!isAdmin) {
           throw new BusinessLogicException(ExceptionCode.NO_AUTHORITY);
       }
       return isAdmin;
    }

    public boolean isCheckCommentOwner (Authentication authentication, Member member) {
        return member.getEmail().equals(authentication.getPrincipal());
    }

}
