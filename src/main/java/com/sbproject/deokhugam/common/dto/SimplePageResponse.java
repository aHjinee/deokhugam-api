package com.sbproject.deokhugam.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SimplePageResponse<T> {
    private int page;
    private int size;
    private int totalPages;
    private long totalItems;
    private List<T> items;
}