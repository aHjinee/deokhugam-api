package com.sbproject.deokhugam.comments.service.impl;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CursorPageResponseCommentDto;
import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.exception.CommentNotFoundException;
import com.sbproject.deokhugam.comments.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.comments.repository.CommentRepository;
import com.sbproject.deokhugam.comments.service.CommentService;
import com.sbproject.deokhugam.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final ReviewRepository reviewRepository;

	@Override
	public CommentDto findComment(UUID commentId) {
		return commentRepository.findByIdAndDeletedAtIsNull(commentId)
			.map(CommentDto::from)
			.orElseThrow(() -> new CommentNotFoundException(commentId));
	}

	@Override
	public CursorPageResponseCommentDto findComments(
		UUID reviewId,
		String direction,
		String cursor,
		Instant after,
		int limit
	) {
		if (!reviewRepository.existsById(reviewId)) {
			throw new ReviewNotFoundException(reviewId);
		}

		if (limit <= 0) {
			throw new IllegalArgumentException("Page size must not be less than one");
		}

		Sort.Direction sortDirection = Sort.Direction.fromString(direction);
		Pageable pageable = PageRequest.of(0, limit + 1, Sort.by(sortDirection, "createdAt"));
		Instant cursorAt = resolveCursorAt(cursor, after);
		List<CommentDto> comments = findCommentEntities(reviewId, sortDirection, cursorAt, pageable)
			.stream()
			.map(CommentDto::from)
			.toList();

		boolean hasNext = comments.size() > limit;
		List<CommentDto> content = hasNext ? comments.subList(0, limit) : comments;
		CommentDto lastComment = content.isEmpty() ? null : content.get(content.size() - 1);
		Instant nextAfter = lastComment == null ? null : lastComment.getCreatedAt();
		String nextCursor = nextAfter == null ? null : nextAfter.toString();

		return new CursorPageResponseCommentDto(
			content,
			nextCursor,
			nextAfter,
			content.size(),
			commentRepository.countByReview_IdAndDeletedAtIsNull(reviewId),
			hasNext
		);
	}

	private List<com.sbproject.deokhugam.comments.entity.Comment> findCommentEntities(
		UUID reviewId,
		Sort.Direction direction,
		Instant cursorAt,
		Pageable pageable
	) {
		if (cursorAt == null) {
			return commentRepository.findByReview_IdAndDeletedAtIsNull(reviewId, pageable);
		}

		if (direction.isDescending()) {
			return commentRepository.findByReview_IdAndDeletedAtIsNullAndCreatedAtLessThan(
				reviewId,
				cursorAt,
				pageable
			);
		}

		return commentRepository.findByReview_IdAndDeletedAtIsNullAndCreatedAtGreaterThan(
			reviewId,
			cursorAt,
			pageable
		);
	}

	private Instant resolveCursorAt(String cursor, Instant after) {
		if (cursor == null || cursor.isBlank()) {
			return after;
		}

		if (after == null) {
			throw new IllegalArgumentException("메인 커서(cursor)가 있는 경우 보조 커서(after)가 필요합니다.");
		}

		try {
			Instant.parse(cursor);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("cursor must be an ISO-8601 instant", e);
		}

		return after;
	}
}
