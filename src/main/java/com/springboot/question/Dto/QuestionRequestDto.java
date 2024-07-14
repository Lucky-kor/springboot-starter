package com.springboot.question.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionRequestDto {

    public Long memberId;
    public String title;
    public String body;
    private String visibilityStatus;

    public QuestionRequestDto() {
    }

    public QuestionRequestDto(Long memberId, String title, String body, String visibilityStatus) {
        this.memberId = memberId;
        this.title = title;
        this.body = body;
        this.visibilityStatus = visibilityStatus;
    }
}
