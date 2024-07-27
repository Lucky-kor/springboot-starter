package com.springboot.page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
    TIME_ASC("오래된 글 순", "TIME_ASC"),
    TIME_DESC("최신글 순", "TIME_DESC"),
    LIKE_ASC("좋아요 적은 순", "LIKE_ASC"),
    LIKE_DESC("좋아요 많은 순", "LIKE_DESC"),
    VIEW_ASC("조회수 적은 순", "VIEW_ASC"),
    VIEW_DESC("좋아요 많은 순", "VIEW_DESC");

    @Getter
    public final String korDes;
    public final String engDes;
}
