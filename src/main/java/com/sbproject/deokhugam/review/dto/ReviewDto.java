package com.sbproject.deokhugam.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 상세 조회 응답 DTO")
public class ReviewDto {

	@Schema(description = "리뷰 고유 식별자 (UUID)", example = "018f6c47-66b0-41f2-a68f-1ea2c8db6cdc")
	private UUID id;

	@Schema(description = "도서 고유 식별자 (UUID)", example = "018f6c47-2adf-4daf-96c6-62ede2538e0e")
	private UUID bookId;

	@Schema(description = "도서 제목", example = "자바 ORM 표준 JPA 프로그래밍")
	private String bookTitle;

	@Schema(description = "도서 표지 이미지 URL", example = "https://example.com/thumbnails/book123.png")
	private String bookThumbnailUrl;

	@Schema(description = "작성자 고유 식별자 (UUID)", example = "018f6c47-96c6-62ede2538e0e-41f2-a68f")
	private UUID userId;

	@Schema(description = "작성자 닉네임", example = "덕후감빌더")
	private String userNickname;

	@Schema(description = "리뷰 본문 내용", example = "JPA의 프록시와 지연 로딩 원리를 이해하는 데 최고의 책입니다!")
	private String content;

	@Schema(description = "리뷰 평점 (1점 ~ 5점 정수)", example = "5", allowableValues = {"1", "2", "3", "4", "5"})
	private Integer rating;

	@Schema(description = "리뷰 좋아요 총 누적 수", example = "42")
	private Integer likeCount;

	@Schema(description = "리뷰에 달린 댓글 총 누적 수", example = "7")
	private Integer commentCount;

	@Schema(description = "현재 로그인한 사용자가 이 리뷰를 좋아요 했는지 여부", example = "true")
	private Boolean likedByMe;

	@Schema(description = "리뷰 최초 생성 일시", example = "2026-04-06T15:04:05Z")
	private Instant createdAt;

	@Schema(description = "리뷰 최종 수정 일시", example = "2026-04-06T18:30:00Z")
	private Instant updatedAt;
}