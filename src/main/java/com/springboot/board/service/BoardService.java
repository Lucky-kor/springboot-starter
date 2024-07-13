package com.springboot.board.service;

import com.springboot.board.entity.Board;
import com.springboot.board.entity.Like;
import com.springboot.board.entity.View;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.repository.LikeRepository;
import com.springboot.board.repository.ViewRepository;
import com.springboot.comment.service.CommentService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.page.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.springboot.board.entity.Board.QuestionStatus.*;
import static com.springboot.page.SortType.*;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final ViewRepository viewRepository;

    public BoardService(BoardRepository boardRepository, MemberService memberService, CommentService commentService, LikeRepository likeRepository, MemberRepository memberRepository, ViewRepository viewRepository) {
        this.boardRepository = boardRepository;
        this.memberService = memberService;
        this.commentService = commentService;
        this.likeRepository = likeRepository;
        this.memberRepository = memberRepository;
        this.viewRepository = viewRepository;
    }

    public Board createBoard (Board board) {
        // 회원인지 아닌지 등록함.
        verifyBoardMember(board);
        Board savedOrder = saveBoard(board);
        // 등록시 등록 날짜 생성.
        return savedOrder;
    }
    public void createLike (Like like) {

        //  어떤 게 null 값이 나왔을 때 어떤 exception 코드를 전해야 하는가?
        // board 에 있는 like 를 확인해야 해서 그런가?
        // 받아온 like 에 board 를 가지고 옴.
        // jpa 가 무조건.
        Optional<Board> board = boardRepository.findById(like.getBoard().getBoardId());
        Board findBoard = board.orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        Optional<Member> member = memberRepository.findById(like.getMember().getMemberId());
        Member findMember = member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Optional<Like> findLike = likeRepository.findByMemberAndBoard(findMember, findBoard);

        if( findLike.isPresent()) {
            // delete 를 해라.
            Like deleteLike = findLike.orElseThrow (() -> new BusinessLogicException(ExceptionCode.LIKE_EXISTS));
            findBoard.deleteLike(deleteLike);
            findMember.deleteLikes(deleteLike);
            findBoard.setLikeCount(findBoard.getLikeCount()-1);
            boardRepository.save(findBoard);
            likeRepository.delete(deleteLike);
        } else {
            Like addlike= new Like();
            addlike.setBoard(findBoard);
            addlike.setMember(findMember);
            likeRepository.save(addlike);
            findBoard.setLikeCount(findBoard.getLikeCount() + 1);
            boardRepository.save(findBoard);
        }
    }

    public Board updateBoard (Board board) {
        // 질문을 등록한 회원만 수정할 수 있음.
        Board findBoard = findVerifiedBoard(board.getBoardId());
        // 질문 수정 시 수정 날짜 업데이트.
        findBoard.setModifiedAt(LocalDateTime.now());

        // 비밀글로 변경할 경우, 상태 변경 수정.
        Optional.ofNullable(board.getVisibilityStatus())
                .ifPresent(visibilityStatus -> findBoard.setVisibilityStatus(visibilityStatus));
        Optional.ofNullable(board.getTitle())
                .ifPresent(title -> findBoard.setTitle(title));
        Optional.ofNullable(board.getContent())
                .ifPresent(content -> findBoard.setContent(content));

        // 답변 완료된 질문은 수정할 수 없음.
        int questionNumber = findBoard.getQuestionStatus().getQuestionNumber();
        if (questionNumber == 2) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_BOARD);
        }

        return boardRepository.save(findBoard);
        // 관리자일 경우 질문 상태 대답 변경.
    }

    public Board findBoard (long boardId, long memberId) {
        // 비밀글이면 질문을 등록한 회원과 관리자만 조회 가능
        // 답변이 존재하면 답변도 함께 조회해야 함.
        Board findBoard = findVerifiedBoard(boardId);
        int questionNumber = findBoard.getQuestionStatus().getQuestionNumber();
        // 이미 삭제 상태인 질문은 조회할 수 없다.

        // view 를 하고 있는 member 를 데리고 와야 하는 것인데??????????
        //
        Optional<Member> member = memberRepository.findById(memberId);
        Member findMember = member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Optional<View> view = viewRepository.findByMemberAndBoard(findMember, findBoard);



        if(view.isPresent()) {
            view.orElseThrow(() -> new BusinessLogicException(ExceptionCode.VIEW_NOT_FOUND));
            // delete 를 해라.
        } else {
            View addView = new View();
            addView.setBoard(findBoard);
            addView.setMember(findMember);
            viewRepository.save(addView);
            findBoard.setViewCount(findBoard.getViewCount() + 1);
            boardRepository.save(findBoard);
        }

        if (questionNumber >= 3) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
        return findBoard;

    }

    public Page<Board> findBoards (int page, int size, String sort) {
        // 여러 건의 질문 목록은 모두 조회 가능.
        // 삭제 상태가 아닌 질문만 조회 가능.
        // 답변도 함께 조회 해야. mapper responsedto . 를 변경. comment mapper 에서

        // 페이지 네이션 처리.
        // 최근 순,
        // 오래 된 순.
        // 정렬 조회.
        SortType sortType = SortType.valueOf(sort);
        Pageable pageable;

        switch (sortType) {
            case TIME_ASC :
                pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").ascending());
                break;
            case TIME_DESC :
                pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
                break;
            case LIKE_ASC :
                pageable = PageRequest.of(page - 1, size, Sort.by("likeCount").ascending());
                break;
            case LIKE_DESC :
                pageable = PageRequest.of(page - 1, size, Sort.by("likeCount").descending());
                break;
            case VIEW_ASC :
                pageable = PageRequest.of(page - 1, size, Sort.by("viewCount").ascending());
                break;
            case VIEW_DESC :
                pageable = PageRequest.of(page - 1, size, Sort.by("viewCount").descending());
                break;
            default:
                pageable = PageRequest.of(page -1, size, Sort.by("boardId").descending());
        }
        return boardRepository
                .findByQuestionStatusNotAndQuestionStatusNot(QUESTION_DELETED, QUESTION_DEACTIVED, pageable);
    }

    public void deleteBoard (long boardId) {
        // 질문 등록한 회원만 가능.


        Board findBoard = findVerifiedBoard(boardId);
        int questionNumber = findBoard.getQuestionStatus().getQuestionNumber();
        if( questionNumber == 1) {
            findBoard.setQuestionStatus(QUESTION_DELETED);
            boardRepository.save(findBoard);
        }
        else if( questionNumber == 2) {
            // 질문 삭제하면 질문 상태만 변경.
            findBoard.setQuestionStatus(QUESTION_DELETED);
            // comment 는 지워져야 함.
            commentService.deleteComment(findBoard.getComment().getCommentId());
            boardRepository.save(findBoard);
        }
        // 이미 삭제상태인 질문은 삭제 불가.
        else if (questionNumber == 3) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_BOARD);
        }
    }

    // board 에 memberId 가 있으면, 수정, 조회, 삭제가 가능하도록 함.
    // 회원인지 아닌지를 조회를 하는 것.
    private void verifyBoardMember (Board board) {
        //
        memberService.findVerifedMember(board.getMember().getMemberId());
    }

    // board 가 있는지 확인.
    public Board findVerifiedBoard (long boardId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board findBoard = optionalBoard.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        return findBoard;
    }

    private Board saveBoard (Board board) {
        return boardRepository.save(board);
    }

}
