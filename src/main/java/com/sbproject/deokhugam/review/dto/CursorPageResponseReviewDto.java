package com.sbproject.deokhugam.review.dto;

import java.time.Instant;
import java.util.List;

import com.querydsl.core.types.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Builder
public class CursorPageResponseReviewDto<T> {
	private String nextCursor;  // 다음 페이지 커서 (id 등)
	private Instant nextAfter;  // 다음 페이지 기준 시각 (createdAt 등)
	private int size;
	private long totalElements;
	private boolean hasNext;
	private List<T> content;
}
