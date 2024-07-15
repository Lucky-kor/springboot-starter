package com.springboot.like.service;


import com.springboot.board.entity.Board;
import com.springboot.board.service.BoardService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.entity.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberService memberService;
    private final BoardService boardService;

    public LikeService(LikeRepository likeRepository, MemberService memberService, BoardService boardService) {
        this.likeRepository = likeRepository;
        this.memberService = memberService;
        this.boardService = boardService;
    }


    public Like createLike(Like like, String email){
        Member findMember = memberService.findVerifiedMember(email);
        Board board = boardService.findVerifiedBoard(like.getBoard().getBoardId());
        like.setMember(findMember);
        like.setBoard(board);

        return likeRepository.save(like);
    }

    public void deleteLike(long likeId, String email){
        memberService.findVerifiedMember(email);
        Optional<Like> optionalLike = likeRepository.findById(likeId);
        Like like = optionalLike.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND));
        boardService.findVerifiedBoard(like.getBoard().getBoardId());

        likeRepository.delete(like);
    }


}
