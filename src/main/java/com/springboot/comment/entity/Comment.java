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

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

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
