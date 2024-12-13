package com.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@Getter
public class MultiResponseDto<T> {
    // data 랑 pageInfo 를 받는데, 이 pageInfo 는 number, size, totalElement, totalPage 를 받음.
    private List<T> data;
    private PageInfo pageInfo;

    public MultiResponseDto(List<T> data, Page page) {
        this.data = data;
        // page 는 인덱스처럼 0부터 시작함. pageInfo 로 response 에 담기 위해서는 + 1 이 필요함.
        this.pageInfo = new PageInfo(page.getNumber() + 1, page.getSize(),
                page.getTotalElements(),page.getTotalPages(), page.getSort().toString());
    }
}
