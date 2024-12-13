package com.springboot.dto;

import com.springboot.page.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@Getter
public class PageInfo {
    private int page;
    private int size;
    private long totalElement;
    private int totalPages;
    private String sortType;
}
