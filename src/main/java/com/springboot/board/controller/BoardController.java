package com.springboot.board.controller;

import com.springboot.board.dto.*;
import com.springboot.board.entity.Board;
import com.springboot.board.entity.Like;
import com.springboot.board.mapper.BoardMapper;
import com.springboot.board.service.BoardService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MemberService memberService;
    private final BoardMapper mapper;

    public BoardController(BoardService boardService, MemberService memberService, BoardMapper mapper) {
        this.boardService = boardService;
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postBoard (@Valid @RequestBody BoardPostDto boardPostDto) {
        // 메퍼로 먼저 감싼 다음에, 서비스에 적용.

        Board board = mapper.boardPostDtoToBoard(boardPostDto);

        Member member = new Member();
        member.setMemberId(boardPostDto.getMemberId());
        board.setMember(member);

        Board createdBoard = boardService.createBoard(board);

        URI location = UriCreator.createUri(BOARD_DEFAULT_URL, createdBoard.getBoardId());
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/like/{board-id}")
    public ResponseEntity postLike (@PathVariable("board-id") @Positive long boardId,
                                    @Valid @RequestBody LikePostDto likePostDto) {
        likePostDto.setBoardId(boardId);
        Like like = mapper.likePostDtoToLike(likePostDto);
        boardService.createLike(like);

        return new ResponseEntity(HttpStatus.OK);
    }


    @PatchMapping("/{board-id}")
    public ResponseEntity patchBoard (@PathVariable("board-id") @Positive long boardId,
                                      @Valid @RequestBody BoardPatchDto boardPatchDto) {
        boardPatchDto.setBoardId(boardId);
        Board board = boardService.updateBoard(mapper.boardPatchDtoTOBoard(boardPatchDto));
        BoardResponseDto boardResponseDto = mapper.boardToBoardResponseDto(board);

        return new ResponseEntity<>(
                new SingleResponseDto<>(boardResponseDto), HttpStatus.OK);
    }

    @GetMapping("/{board-id}")
    public ResponseEntity getBoard ( @PathVariable("board-id") @Positive long boardId,
                                     @Valid @RequestBody BoardGetDto boardGetDto
    ) {
        Board board = boardService.findBoard(boardId, boardGetDto.getMemberId());
        BoardResponseDto boardResponseDto = mapper.boardToBoardResponseDto(board);
        return new ResponseEntity<>(
                new SingleResponseDto<>(boardResponseDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getBoards (@Positive @RequestParam int page,
                                      @Positive @RequestParam int size,
                                     // desc, asc
                                     @RequestParam String sort) {
        Page<Board> pageBoards = boardService.findBoards(page, size, sort);
        List<Board> boards = pageBoards.getContent();
        List<BoardResponseDto> responseDtos = mapper.boardsToBoardResponseDtos(boards);

        return new ResponseEntity<>(
                new MultiResponseDto<>(responseDtos,pageBoards), HttpStatus.OK );
    }


    // 질문 삭제 상태.
    // 회원 탈퇴시 질문 비활성화. >> memberService
    @DeleteMapping("{member-id}")
    public ResponseEntity deleteBoard ( @PathVariable("member-id") @Positive long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


}
