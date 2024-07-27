package com.springboot.comment.entity;

import com.springboot.audit.Auditable;
import com.springboot.board.entity.Board;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    public void setMember(Member member) {
        this.member = member;
        if (!member.getComments().contains(this)) {
            member.setComments(this);
        }
    }

    @OneToOne(cascade = CascadeType.MERGE)
    //persist : 처음에 만들때 씀.
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    public void setBoard (Board board) {
        this.board = board;
        if(board.getComment() != this) {
            board.setComment(this);
        }
    }
    public void deleteBoard(Board board) {
        this.board = null;
        if(board.getComment() == this) {
            board.deleteComment(this);
        }
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private VisibilityStatus visibilityStatus = VisibilityStatus.PUBLIC;

    public enum VisibilityStatus {
        PUBLIC("공개글"),
        SECRET("비밀글");

        @Getter
        private String visibilityStatus;

        VisibilityStatus(String visibilityStatus) {
            this.visibilityStatus = visibilityStatus;
        }
    }
}
