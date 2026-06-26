package com.sbproject.deokhugam.comments.service;

import java.time.Instant;
import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;

public interface CommentService {

	CommentDto findComment(UUID commentId);

	SlicePageResponse<CommentDto> findComments(
		UUID reviewId,
		String direction,
		String cursor,
		Instant after,
		int limit
	);
}
