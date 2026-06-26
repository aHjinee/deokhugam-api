package com.sbproject.deokhugam.common.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SlicePageResponse<T> {
	private List<T> content;
	private String nextCursor;
	private Instant nextAfter;
	private int size;
	private long totalElements;
	private boolean hasNext;
}
