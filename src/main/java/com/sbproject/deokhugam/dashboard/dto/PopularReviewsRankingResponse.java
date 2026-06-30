package com.sbproject.deokhugam.dashboard.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularReviewsRankingResponse {

	private int rank;

	@JsonProperty("id")
	private String reviewId;

	private String userId;
	private String bookId;

	@JsonProperty("userNickname")
	private String nickname;

	@JsonProperty("bookTitle")
	private String title;

	@JsonProperty("bookThumbnailUrl")
	private String thumbnailUrl;

	private String content;
	private double rating;
	private double score;
	private int likeCount;
	private int commentCount;
	private Instant createdAt;

	public static PopularReviewsRankingResponse from(
		PopularReviewsDocument.Ranking ranking
	) {
		return new PopularReviewsRankingResponse(
			ranking.getRank(),
			ranking.getReviewId(),
			ranking.getUserId(),
			ranking.getBookId(),
			ranking.getNickname(),
			ranking.getTitle(),
			ranking.getThumbnailUrl(),
			ranking.getContent(),
			ranking.getRating(),
			ranking.getScore(),
			ranking.getLikeCount(),
			ranking.getCommentCount(),
			ranking.getCreatedAt()
		);
	}
}