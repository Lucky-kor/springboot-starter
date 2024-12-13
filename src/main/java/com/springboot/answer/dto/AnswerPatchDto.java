package com.springboot.answer.dto;

import lombok.Getter;

import javax.validation.constraints.Positive;

@Getter
public class AnswerPatchDto {
    private long answerId;

    private String content;

    public void setAnswerId(long answerId){
        this.answerId = answerId;
    }

}
