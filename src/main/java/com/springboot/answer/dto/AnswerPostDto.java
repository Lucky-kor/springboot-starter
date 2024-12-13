package com.springboot.answer.dto;


import lombok.Getter;

import javax.validation.constraints.Positive;

@Getter
public class AnswerPostDto {

    @Positive
    private long boardId;

    private String content;

}
