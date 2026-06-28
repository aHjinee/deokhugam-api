package com.sbproject.deokhugam.review.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewCreateRequest;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;
import com.sbproject.deokhugam.review.service.ReviewLikeService;
import com.sbproject.deokhugam.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "리뷰 관리", description = "리뷰 등록, 조회, 수정, 삭제 및 좋아요 관련 API")
public class ReviewController {

	private final ReviewService reviewService;
	private final ReviewLikeService reviewLikeService;

	@GetMapping
	@Operation(summary = "리뷰 목록 조회", description = "조건에 맞는 리뷰 목록을 페이징 조회합니다.")
	public ResponseEntity<SlicePageResponse<ReviewDto>> getReviewList(
		@RequestHeader(value = "Deokhugam-Request-User-ID", required = false) UUID deokhugamRequestUserId,
		@ModelAttribute ReviewSearchRequest request
	) {
		request.setDeokhugamRequestUserId(deokhugamRequestUserId);
		return ResponseEntity.ok(reviewService.findAll(request));
	}

	@PostMapping
	@Operation(summary = "리뷰 등록", description = "새로운 도서 리뷰를 등록합니다.")
	public ResponseEntity<ReviewDto> createReview(
		@Valid @RequestBody ReviewCreateRequest request
	) {
		return null;
	}

	@GetMapping("/{reviewId}")
	@Operation(summary = "리뷰 상세 정보 조회", description = "특정 리뷰의 상세 정보를 단건 조회합니다.")
	public ResponseEntity<ReviewDto> getReviewDetail(
		@PathVariable("reviewId") UUID reviewId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID deokhugamRequestUserId
	) {
		ReviewDto reviewDto = reviewService.findById(reviewId, deokhugamRequestUserId);
		return ResponseEntity.ok(reviewDto);
	}

	@PatchMapping("/{reviewId}")
	@Operation(summary = "리뷰 수정", description = "기존에 작성한 리뷰의 본문 및 평점을 수정합니다.")
	public ResponseEntity<ReviewDto> updateReview(
		@PathVariable("reviewId") UUID reviewId,
		@Valid @RequestBody ReviewCreateRequest request, // 상황에 따라 UpdateRequest 분리 가능
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID deokhugamRequestUserId
	) {
		return null;
	}

	@DeleteMapping("/{reviewId}")
	@Operation(summary = "리뷰 논리 삭제", description = "리뷰의 deleted_at 상태를 업데이트하여 논리 삭제 처리합니다.")
	public ResponseEntity<Void> deleteReviewLogical(
		@PathVariable("reviewId") UUID reviewId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID deokhugamRequestUserId
	) {
		return null;
	}

	@DeleteMapping("/{reviewId}/hard")
	@Operation(summary = "리뷰 물리 삭제", description = "데이터베이스에서 리뷰 레코드를 영구적으로 삭제(Hard Delete)합니다.")
	public ResponseEntity<Void> deleteReviewPhysical(
		@PathVariable("reviewId") UUID reviewId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID deokhugamRequestUserId
	) {
		return null;
	}

	@PostMapping("/{reviewId}/like")
	@Operation(summary = "리뷰 좋아요", description = "특정 리뷰에 좋아요를 반영하거나 토글 처리합니다.")
	public ResponseEntity<Void> toggleReviewLike(
		@PathVariable("reviewId") UUID reviewId,
		@RequestHeader(value = "Deokhugam-Request-User-ID") UUID deokhugamRequestUserId
	) {
		return null;
	}
}