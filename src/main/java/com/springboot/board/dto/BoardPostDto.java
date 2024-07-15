package com.springboot.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
public class BoardPostDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
