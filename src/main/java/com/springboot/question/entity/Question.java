package com.springboot.question.entity;

import com.springboot.comment.entity.Comment;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String body;

    @Column(length = 20, nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private Boolean isSecret = false;

    @Column(nullable = false)
    private Boolean isNew;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void addMember(Member member) {
        this.member = member;
    }

    public enum QuestionStatus {
        QUESTION_REGISTERED("질문 등록 상태"),
        QUESTION_ANSWERED("답변 완료 상태"),
        QUESTION_DELETED("질문 삭제 상태"),
        QUESTION_DEACTIVATED("질문 비활성화 상태");

        @Getter
        private String status;

        QuestionStatus(String status) {
            this.status = status;
        }
    }

    @Enumerated(EnumType.STRING)
    private VisibilityStatus visibilityStatus;

    public enum VisibilityStatus {
        PUBLIC,
        SECRET
    }

    public void addComment(Comment comment) {
        this.comment = comment;
        comment.setQuestion(this);
    }
}
