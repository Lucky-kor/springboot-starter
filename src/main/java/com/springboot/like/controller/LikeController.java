package com.springboot.like.controller;

import com.springboot.like.dto.LikePostDto;
import com.springboot.like.entity.Like;
import com.springboot.like.mapper.LikeMapper;
import com.springboot.like.service.LikeService;
import com.springboot.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;


@RestController
@RequestMapping("/v11/likes")
@Validated
public class LikeController {
    private final static String LIKES_DEFAULT_URL = "/v11/likes";
    private final LikeService likeService;
    private final LikeMapper mapper;

    public LikeController(LikeService likeService, LikeMapper mapper) {
        this.likeService = likeService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postLike(@Valid @RequestBody LikePostDto likePostDto,
                                    Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        Like like = likeService.createLike(mapper.likePostDtoToLike(likePostDto),email);

        if(like == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        URI location = UriCreator.createUri(LIKES_DEFAULT_URL, like.getLikeId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{like-id}")
    public ResponseEntity deleteLike(@PathVariable("like-id") @Positive long likeId,
                                     Authentication authentication){
        String email = (String) authentication.getPrincipal();
        likeService.deleteLike(likeId, email);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
