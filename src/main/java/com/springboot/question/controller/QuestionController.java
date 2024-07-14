package com.springboot.question.controller;

import com.springboot.question.Dto.QuestionRequestDto;
import com.springboot.question.service.QuestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public void createQuestion(@RequestBody QuestionRequestDto questionRequestDto) {
        questionService.createQuestion(questionRequestDto.getMemberId(), questionRequestDto.getTitle(), questionRequestDto.getBody(), questionRequestDto.getVisibilityStatus());
    }
}
