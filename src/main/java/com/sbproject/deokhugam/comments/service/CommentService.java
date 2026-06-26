package com.sbproject.deokhugam.comments.service;

import java.time.Instant;
import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CursorPageResponseCommentDto;
import com.sbproject.deokhugam.comments.dto.CommentDto;

public interface CommentService {

	CommentDto findComment(UUID commentId);

	CursorPageResponseCommentDto findComments(
		UUID reviewId,
		String direction,
		String cursor,
		Instant after,
		int limit
	);
}
