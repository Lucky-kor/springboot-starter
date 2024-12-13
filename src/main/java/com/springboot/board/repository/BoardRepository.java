package com.springboot.board.repository;

import com.springboot.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByQuestionStatusNotAndQuestionStatusNot(Board.QuestionStatus questionStatus1, Board.QuestionStatus QuestionStatus2);
    Page<Board> findByQuestionStatusNotAndQuestionStatusNot(Board.QuestionStatus questionStatus1, Board.QuestionStatus QuestionStatus2, Pageable pageable);

}
