package com.springboot.answer.service;


import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.board.entity.Board;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.service.BoardService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@Slf4j
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final BoardService boardService;
    private final MemberService memberService;

    public AnswerService(AnswerRepository answerRepository, BoardService boardService, MemberService memberService) {
        this.answerRepository = answerRepository;
        this.boardService = boardService;
        this.memberService = memberService;
    }

    public Answer createAnswer(Answer answer, String email){
        verifiedAnswer(answer);

        Member member = memberService.findVerifiedMember(email);
        answer.setMember(member);
        updateBoard(answer);

        Answer answer1 =answerRepository.save(answer);
        Board board = boardService.findVerifiedBoard(answer.getBoard().getBoardId());
        board.setBoardStatus(Board.BoardStatus.QUESTION_REGISTERED);
        boardService.updateBoard(board);

        return answerRepository.save(answer);
    }
    public Answer updateAnswer(Answer answer, String email){
        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());

        if(!findAnswer.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_ANSWER);
        }

        Optional.ofNullable(answer.getContent())
                .ifPresent(content -> findAnswer.setContent(content));

        return answerRepository.save(findAnswer);
    }

    public Answer findAnswer(long answerId){
        return findVerifiedAnswer(answerId);
    }

    public Page<Answer> findAnswers(int page, int size){
        return answerRepository.findAll(PageRequest.of(page,size, Sort.by("answerId").descending()));
    }
    public void deleteAnswer(long answerId, String email){
        Answer answer = findVerifiedAnswer(answerId);
        Board findBoard = boardService.findVerifiedBoard(answer.getBoard().getBoardId());

        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());

        if(!findAnswer.getMember().getEmail().equals(email)){
            throw new BusinessLogicException(ExceptionCode.NOT_YOUR_ANSWER);
        }

        findBoard.setAnswer(null);
        boardService.updateBoard(findBoard);
        answerRepository.delete(findVerifiedAnswer(answerId));
    }



    private Answer findVerifiedAnswer(long answerId){
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        Answer answer = optionalAnswer.orElseThrow(()->
               new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));

        return answer;
    }

    private void verifiedAnswer(Answer answer){
        boardService.findVerifiedBoard(answer.getBoard().getBoardId());
    }
    private void updateBoard(Answer answer){
        Board board = boardService.findBoard(answer.getBoard().getBoardId());

        board.setAnswer(answer);
        boardService.updateBoard(board);
    }

}
