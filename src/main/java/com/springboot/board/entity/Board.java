package com.springboot.board.entity;

import com.springboot.audit.Auditable;
import com.springboot.comment.entity.Comment;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Board extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private int likeCount = 0;

    @Column
    private int viewCount = 0;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    public void setMember(Member member) {
        this.member = member;
        if (!member.getBoards().contains(this)) {
            member.setBoards(this);
        }
    }

    @OneToOne(mappedBy = "board")
    private Comment comment;
    public void setComment(Comment comment) {
        this.comment = comment;
        if (comment.getBoard() != this) {
            comment.setBoard(this);
        }
    }

    @OneToOne(cascade = CascadeType.ALL)
    private Like like;
    public void setLike(Like like) {
        this.like = like;
        if (like.getBoard() != this) {
            like.setBoard(this);
        }
    }
    public void deleteLike(Like like) {
        this.like = null;
        if (like.getBoard() == this) {
            like.deleteBoard(this);
        }
    }

    @OneToOne
    private View view;
    public void setView (View view) {
        this.view = view;
        if(view.getBoard() != this) {
            view.setBoard(this);
        }
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private VisibilityStatus visibilityStatus = VisibilityStatus.PUBLIC;

    public enum QuestionStatus {
        QUESTION_REGISTERED(1, "질문 등록"),
        QUESTION_ANSWERED(2, "답변 완료"),
        QUESTION_DELETED(3, "질문 삭제"),
        // 탈퇴했을 때.
        QUESTION_DEACTIVED(4, "질문 비활성화");

        @Getter
        private int QuestionNumber;

        @Getter
        private String Status;

        QuestionStatus(int QuestionNumber, String questionStatus) {
            this.QuestionNumber = QuestionNumber;
            this.Status = Status;
        }
    }

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