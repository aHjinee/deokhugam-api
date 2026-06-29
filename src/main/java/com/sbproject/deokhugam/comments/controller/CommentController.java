package com.sbproject.deokhugam.comments.controller;

import java.time.Instant;
import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CommentCreateRequest;
import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.dto.CommentUpdateRequest;
import com.sbproject.deokhugam.comments.service.CommentService;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentDto> createComment(
		@Valid @RequestBody CommentCreateRequest request,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentService.createComment(request, requestUserId));
	}

	@GetMapping
	public ResponseEntity<SlicePageResponse<CommentDto>> findComments(
		@RequestParam UUID reviewId,
		@RequestParam(defaultValue = "DESC") String direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant after,
		@RequestParam(defaultValue = "50") int limit
	) {
		return ResponseEntity.ok(commentService.findComments(reviewId, direction, cursor, after, limit));
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<CommentDto> findComment(@PathVariable UUID commentId) {
		return ResponseEntity.ok(commentService.findComment(commentId));
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentDto> updateComment(
		@PathVariable UUID commentId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
		@Valid @RequestBody CommentUpdateRequest request
	) {
		return ResponseEntity.ok(commentService.updateComment(commentId, request, requestUserId));
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(
		@PathVariable UUID commentId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		commentService.deleteComment(commentId, requestUserId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{commentId}/hard")
	public ResponseEntity<Void> hardDeleteComment(
		@PathVariable UUID commentId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		commentService.hardDeleteComment(commentId, requestUserId);
		return ResponseEntity.noContent().build();
	}
}
