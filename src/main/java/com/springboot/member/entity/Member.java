package com.springboot.member.entity;

import com.springboot.audit.Auditable;
import com.springboot.board.entity.Like;
import com.springboot.board.entity.View;
import com.springboot.comment.entity.Comment;
import com.springboot.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Member extends Auditable {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    @OneToMany (mappedBy = "member")
    private List<Board> boards = new ArrayList<>();
    public void setBoards (Board board) {
        this.boards.add(board);
        if(board.getMember() != this) {
            board.setMember(this);
        }
    }

    @OneToMany (mappedBy = "member", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();
    public void setLikes(Like like) {
        this.likes.add(like);
        if(like.getMember() != this) {
            like.setMember(this);
        }
    }
    public void deleteLikes(Like like) {
        this.likes.remove(like);
        if(like.getMember() == this) {
            like.deleteMember(this);
        }
    }

    @OneToMany (mappedBy = "member")
    private List<View> views = new ArrayList<>();
    public void setViews (View view) {
        this.views.add(view);
        if(view.getMember() == this) {
            view.setMember(this);
        }
    }

    public enum MemberStatus {
        MEMBER_ACTIVE("활동중"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }


}
