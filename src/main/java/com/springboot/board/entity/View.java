package com.springboot.board.entity;

import com.springboot.audit.Auditable;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class View{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    public void setBoard (Board board) {
        this.board = board;
        if(board.getView() != this) {
            board.setView(this);
        }
    }

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    public void setMember (Member member) {
        this.member = member;
        if(!member.getViews().contains(this)) {
            member.setViews(this);
        }
    }
}
