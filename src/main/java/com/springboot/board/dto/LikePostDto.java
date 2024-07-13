package com.springboot.board.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LikePostDto {

    private Long memberId;
    private Long boardId;

}
