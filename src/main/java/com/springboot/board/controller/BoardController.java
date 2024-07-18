package com.springboot.board.controller;


import com.springboot.answer.entity.Answer;
import com.springboot.board.dto.BoardPatchDto;
import com.springboot.board.dto.BoardPostDto;
import com.springboot.board.entity.Board;
import com.springboot.board.mapper.BoardMapper;
import com.springboot.board.service.BoardService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v11/boards")
@Validated
public class BoardController {
    private final static String BOARD_DEFAULT_URL = "/v11/boards";
    private final BoardService boardService;
    private final BoardMapper mapper;

    public BoardController(BoardService boardService, BoardMapper mapper) {
        this.boardService = boardService;
        this.mapper = mapper;

    }

    @PostMapping
    public ResponseEntity postBoard(@Valid @RequestBody BoardPostDto boardPostDto,
                                    Authentication authentication){
        String email = (String) authentication.getPrincipal();

        Board board = boardService.createBoard(mapper.boardPostDtoToBoard(boardPostDto),email);

        URI location = UriCreator.createUri(BOARD_DEFAULT_URL, board.getBoardId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity patchBoard(@PathVariable("board-id") @Positive long boardId,
                                     @Valid @RequestBody BoardPatchDto boardPatchDto,
                                     Authentication authentication){
        boardPatchDto.setBoardId(boardId);
        if (authentication == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = (String) authentication.getPrincipal();

        System.out.println("Email: " + email);

        Board board =
                boardService.updateBoard(mapper.boardPatchDtoToBoard(boardPatchDto),email);
        return new ResponseEntity(
                new SingleResponseDto<>(mapper.boardToBoardResponseDto(board)), HttpStatus.OK);
    }

    @GetMapping("/{board-id}")
    public ResponseEntity patchBoard(@PathVariable("board-id") @Positive long boardId,
                                     Authentication authentication){
        Board board = boardService.findBoard(boardId, authentication);

        return new ResponseEntity(new SingleResponseDto<>(mapper.boardToBoardResponseDto(board)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getBoards(@Positive @RequestParam int page,
                                    @Positive @RequestParam int size){
        Page<Board> pageBoards = boardService.findBoards(page -1, size);
        List<Board> boards = pageBoards.getContent();
        return new ResponseEntity(
                new MultiResponseDto<>(mapper.boardsToBoardResponseDtos(boards),pageBoards), HttpStatus.OK
        );
    }

    @DeleteMapping("/{board-id}")
    public ResponseEntity cancleBoard(@PathVariable("board-id") @Positive long boardId,
                                      Authentication authentication){
        String email = (String) authentication.getPrincipal();
        boardService.deleteBoard(boardId,email);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
