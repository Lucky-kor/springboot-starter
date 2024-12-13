package com.springboot.board.dto;

import com.springboot.board.entity.Board;
import com.springboot.comment.dto.CommentResponseDto;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class BoardResponseDto {
    private long boardId;
    private long memberId;
    private String title;
    private String content;
    private int likeCount ;
    private int viewCount ;
    private Board.QuestionStatus questionStatus;
    private Board.VisibilityStatus visibilityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private CommentResponseDto boardComment;

}
