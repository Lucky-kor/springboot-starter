package com.springboot.board.repository;

import com.springboot.board.entity.Board;
import com.springboot.board.entity.View;
import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewRepository extends JpaRepository<View, Long> {
    Optional<View>  findByBoardAndMember(Board board, Member member);
}
