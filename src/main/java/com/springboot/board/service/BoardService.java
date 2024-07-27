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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Board createBoard (Board board, Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            throw new BusinessLogicException(ExceptionCode.NO_AUTHORITY);
        } else {
            String email = (String) authentication.getPrincipal();
            Member member = memberService.findVerifiedMember(email);
            board.setMember(member);

            return boardRepository.save(board);
        }
    }
    public void createLike (long boardId, Authentication authentication) {

        //  어떤 게 null 값이 나왔을 때 어떤 exception 코드를 전해야 하는가?
        // board 에 있는 like 를 확인해야 해서 그런가?
        // 받아온 like 에 board 를 가지고 옴.
        // jpa 가 무조건.
        Optional<Board> board = boardRepository.findById(boardId);
        Board findBoard = board.orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        Optional<Member> member = memberRepository.findByEmail((String) authentication.getPrincipal());
        Member findMember = member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Optional<Like> findLike = likeRepository.findByMemberAndBoard(findMember, findBoard);

        if( findLike.isPresent()) {
            // delete 를 해라.
            Like deleteLike = findLike.orElseThrow (() -> new BusinessLogicException(ExceptionCode.LIKE_EXISTS));
            findBoard.deleteLike(deleteLike);
            findMember.deleteLikes(deleteLike);
            findBoard.decreaseCount();
            boardRepository.save(findBoard);
            likeRepository.delete(deleteLike);
        } else {
            Like addlike= new Like();
            addlike.setBoard(findBoard);
            addlike.setMember(findMember);
            findBoard.increasedCount();
            likeRepository.save(addlike);
        }
    }

    public Board updateBoard (Board board, Authentication authentication) {
        // 질문을 등록한 회원만 수정할 수 있음.
        String email = (String) authentication.getPrincipal();
        Member member = memberService.findVerifiedMember(email);
        Board findBoard = findVerifiedBoard(board.getBoardId());

        if(findBoard.getMember() == member ) {
            // 질문 수정 시 수정 날짜 업데이트.
            findBoard.setModifiedAt(LocalDateTime.now());
            // 비밀글로 변경할 경우, 상태 변경 수정.
            Optional.ofNullable(board.getVisibilityStatus())
                    .ifPresent(visibilityStatus -> findBoard.setVisibilityStatus(visibilityStatus));
            Optional.ofNullable(board.getTitle())
                    .ifPresent(title -> findBoard.setTitle(title));
            Optional.ofNullable(board.getContent())
                    .ifPresent(content -> findBoard.setContent(content));
        } else {
            new BusinessLogicException(ExceptionCode.DIFFERENT_USER);
        }
        // 답변 완료된 질문은 수정할 수 없음.
        int questionNumber = findBoard.getQuestionStatus().getQuestionNumber();
        if (questionNumber == 2) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_BOARD);
        }

        return boardRepository.save(findBoard);

    }

    public Board findBoard (long boardId, Authentication authentication) {
        // 비밀글이면 질문을 등록한 회원과 관리자만 조회 가능.
        // 답변이 존재하면 답변도 함께 조회해야 함.
        Board findBoard = findVerifiedBoard(boardId);
        // 비밀글을 제외한 다른 글들은 회원과 관리자 모두가 봐야 함.

        // 비밀글이면
        if(findBoard.getVisibilityStatus().equals(Board.VisibilityStatus.SECRET)) {
            // 관리자와 질문을 등록한 회원은 볼 수 있음. 게시글의 작성자와 principal 이 같은지 확인.
            if(isCheckBoardOwnerAndAdmin(authentication, findBoard.getMember())) {
                createdView(authentication,findBoard);
                return findBoard;
            } else {
                throw new BusinessLogicException(ExceptionCode.NO_AUTHORITY);
            }
        } else {
            // 공개 일 때에는 다른 회원의 글도 볼 수 있어야 함.......
            // 그냥 조회하면 되는 건가?
            // 이거는 뷰에 대한 로직.
            createdView (authentication, findBoard);
            // 질문삭제, 비활성화 된 글은 보이도록 하지 않음.
            // 이미 삭제 상태인 질문은 조회할 수 없다.
            int questionNumber = findBoard.getQuestionStatus().getQuestionNumber();
            if (questionNumber >= 3) {
                throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
            }
            return findBoard;
        }
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

    public void deleteBoard (long boardId, Authentication authentication) {
        // 질문 등록한 회원만 가능.
        Board findBoard = findVerifiedBoard(boardId);
        if(isCheckBoardOwner(authentication, findBoard.getMember())){
            int questionNumber = findBoard.getQuestionStatus().getQuestionNumber();
            if( questionNumber == 1) {
                findBoard.setQuestionStatus(QUESTION_DELETED);
                boardRepository.save(findBoard);
            }
            else if( questionNumber == 2) {
                // 질문 삭제하면 질문 상태만 변경.
                findBoard.setQuestionStatus(QUESTION_DELETED);
                // comment 는 지워져야 함.
                findBoard.deleteComment(findBoard.getComment());
                boardRepository.save(findBoard);
            }
            // 이미 삭제상태인 질문은 삭제 불가.
            else if (questionNumber == 3) {
                throw new BusinessLogicException(ExceptionCode.CANNOT_DELETE_BOARD);
            }
        }else {
            throw new BusinessLogicException(ExceptionCode.DIFFERENT_USER);
        }

    }

    // board 에 memberId 가 있으면, 수정, 조회, 삭제가 가능하도록 함.
    // 회원인지 아닌지를 조회를 하는 것.
    private void verifyBoardMember (Board board) {

        memberService.findVerifedMember(board.getMember().getMemberId());
    }

    // board 가 있는지 확인.
    public Board findVerifiedBoard (long boardId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board findBoard = optionalBoard.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        return findBoard;
    }

    private boolean isCheckBoardOwnerAndAdmin(Authentication authentication, Member member) {
        return member.getEmail().equals(authentication.getPrincipal())
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public boolean isCheckBoardOwner(Authentication authentication, Member member) {
        return member.getEmail().equals(authentication.getPrincipal());
    }



    private void createdView (Authentication authentication, Board board) {
        String email = (String) authentication.getPrincipal();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member findMember = optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Optional<View> view = viewRepository.findByMemberAndBoard(findMember, board);

        if(view.isPresent()) {
            view.orElseThrow(() -> new BusinessLogicException(ExceptionCode.VIEW_NOT_FOUND));
        } else {
            View addView = new View();
            addView.setBoard(board);
            addView.setMember(findMember);
            board.setViewCount(board.getViewCount() + 1);
            viewRepository.save(addView);
        }

    }

}
