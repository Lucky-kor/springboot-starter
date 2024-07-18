package com.springboot.board.service;


import com.springboot.board.entity.Board;
import com.springboot.board.repository.BoardRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;


    public BoardService(BoardRepository boardRepository, MemberService memberService) {
        this.boardRepository = boardRepository;
        this.memberService = memberService;
    }

    public Board createBoard(Board board){
        verifyBoard(board);

        return boardRepository.save(board);
    }

    public Board createBoard(Board board, String email){

        Member member = memberService.findVerifiedMember(email);

        board.setMember(member);

        return boardRepository.save(board);
    }

    public Board updateBoard(Board board){
        Board findBoard = findVerifiedBoard(board.getBoardId());

        Optional.ofNullable(board.getTitle())
                .ifPresent(title -> findBoard.setTitle(title));
        Optional.ofNullable(board.getContent())
                .ifPresent(content -> findBoard.setContent(content));

        return boardRepository.save(findBoard);
    }

    public Board updateBoard(Board board, String email){
        Board findBoard = findVerifiedBoard(board.getBoardId());

        if(!findBoard.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_BOARD);
        }

        Optional.ofNullable(board.getTitle())
                .ifPresent(title -> findBoard.setTitle(title));
        Optional.ofNullable(board.getContent())
                .ifPresent(content -> findBoard.setContent(content));

        return boardRepository.save(findBoard);
    }

    public Board findBoard(long boardId,Authentication authentication){
        Board findBoard = findVerifiedBoard(boardId);
        if(findBoard.getBoardSecret().equals(Board.BoardSecret.SECRET_BOARD)){
           boolean isAdmin = authentication.getAuthorities().stream()
                   .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            if(isAdmin){
               return findBoard;
           }
           else{
               throw new BusinessLogicException(ExceptionCode.ONLY_ADMIN);
           }
        }

        return findVerifiedBoard(boardId);
    }
    public Page<Board> findBoards(int page, int size){
        return boardRepository.findAll(PageRequest.of(page,size, Sort.by("boardId").descending()));
    }

    public void deleteBoard(long boardId, String email){
        Board findBoard = findVerifiedBoard(boardId);

        if(!findBoard.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_BOARD);
        }

        findBoard.setBoardStatus(Board.BoardStatus.QUESTION_DELETED);
    }

    public Board findVerifiedBoard(long boardId){
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        Board findBoard =
                optionalBoard.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        return findBoard;
    }


    private Board saveBoard(Board board){
        return boardRepository.save(board);
    }

    private void verifyBoard(Board board){
        Member findMember = memberService.findVerifiedMember(board.getMember().getMemberId());
        if(findMember.getMemberStatus().equals(Member.MemberStatus.MEMBER_QUIT)){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
    }




}
