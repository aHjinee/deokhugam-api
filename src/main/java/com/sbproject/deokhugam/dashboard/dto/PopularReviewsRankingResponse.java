package com.sbproject.deokhugam.dashboard.dto;

import java.time.Instant;
import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularReviewsRankingResponse {

	private String id;
	private String reviewId;
	private String bookId;
	private String bookTitle;
	private String thumbnailUrl;
	private String userId;
	private String userNickname;
	private String reviewContent;
	private double reviewRating;
	private PeriodType period;
	private Instant createdAt;
	private int rank;
	private double score;
	private int likeCount;
	private int commentCount;

	public static PopularReviewsRankingResponse from(
		PopularReviewsDocument.Ranking ranking,
		PopularReviewsDocument document
	) {
		return new PopularReviewsRankingResponse(
			ranking.getReviewId(),
			ranking.getReviewId(),
			ranking.getBookId(),
			ranking.getTitle(),
			ranking.getThumbnailUrl(),
			ranking.getUserId(),
			ranking.getNickname(),
			ranking.getContent(),
			ranking.getRating(),
			document.getPeriodType(),
			ranking.getCreatedAt(),
			ranking.getRank(),
			ranking.getScore(),
			ranking.getLikeCount(),
			ranking.getCommentCount()
		);
	}
}