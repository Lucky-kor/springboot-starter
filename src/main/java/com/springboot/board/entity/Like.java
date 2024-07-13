package com.springboot.board.entity;

import com.springboot.audit.Auditable;
import com.springboot.comment.entity.Comment;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapping;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    public void setBoard (Board board) {
        this.board = board;
        if(board.getLike() != this) {
            board.setLike(this);
        }
    }
    public void deleteBoard (Board board) {
        this.board = null;
        if(board.getLike() == this) {
            board.deleteLike(this);
        }
    }
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    public void setMember (Member member) {
        this.member = member;
        if(!member.getLikes().contains(this)) {
            member.setLikes(this);
        }
    }
    public void deleteMember (Member member) {
        this.member = null;
        if(member.getLikes().contains(this)) {
            member.deleteLikes(this);
        }
    }
}
