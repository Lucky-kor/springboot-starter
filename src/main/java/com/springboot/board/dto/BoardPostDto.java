package com.springboot.board.dto;

import com.springboot.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
public class BoardPostDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Enumerated(value = EnumType.STRING)
    private Board.BoardSecret boardSecret;
}
