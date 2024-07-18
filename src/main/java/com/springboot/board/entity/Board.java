package com.springboot.board.entity;


import com.springboot.answer.entity.Answer;
import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "BOARDS")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BoardStatus boardStatus = BoardStatus.QUESTION_REGISTERED;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt = LocalDateTime.now();


    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BoardSecret boardSecret = BoardSecret.PUBLIC_BOARD;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    List<Like> likes = new ArrayList<>();

    @Column
    private Integer likeCount = 0;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setAnswer(Answer answer){
        if(answer == null){
            this.answer =null;
            return;
        }
        this.answer = answer;
        if(answer.getBoard() != this){
            answer.setBoard(this);
        }
    }

    public enum BoardStatus{
        QUESTION_REGISTERED(1, "질문 등록 상태"),
        QUESTION_ANSWERED(2, "답변 완료 상태"),
        QUESTION_DELETED(3, "질문 삭제 상태"),
        QUESTION_DEACTIVED(4, "질문 비활성화 상태");

        @Getter
        private int stepNumber;

        @Getter
        private String stepDescription;

        BoardStatus(int stepNumber, String stepDescription) {
            this.stepNumber = stepNumber;
            this.stepDescription = stepDescription;
        }
    }

    public enum BoardSecret{
        SECRET_BOARD(1, "비밀 글"),
        PUBLIC_BOARD(2, "공개 글");

        @Getter
        private int stepNumber;

        @Getter
        private String  stepDescription;

        BoardSecret(int stepNumber, String stepDescription) {
            this.stepNumber = stepNumber;
            this.stepDescription = stepDescription;
        }
    }


}
