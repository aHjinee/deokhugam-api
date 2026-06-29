package com.sbproject.deokhugam.comments.service;

import java.time.Instant;
import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CommentCreateRequest;
import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.dto.CommentUpdateRequest;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;

public interface CommentService {

	CommentDto createComment(CommentCreateRequest request, UUID requestUserId);

	CommentDto findComment(UUID commentId);

	CommentDto updateComment(UUID commentId, CommentUpdateRequest request, UUID requestUserId);

	SlicePageResponse<CommentDto> findComments(
		UUID reviewId,
		String direction,
		String cursor,
		Instant after,
		int limit
	);
}
