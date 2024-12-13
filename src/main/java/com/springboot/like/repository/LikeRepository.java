package com.springboot.like.repository;

import com.springboot.board.entity.Board;
import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByBoardAndMember(Board board, Member member);
}
