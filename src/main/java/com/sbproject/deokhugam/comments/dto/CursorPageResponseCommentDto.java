package com.sbproject.deokhugam.comments.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorPageResponseCommentDto {

	private List<CommentDto> content;
	private String nextCursor;
	private Instant nextAfter;
	private int size;
	private long totalElements;
	private boolean hasNext;
}
