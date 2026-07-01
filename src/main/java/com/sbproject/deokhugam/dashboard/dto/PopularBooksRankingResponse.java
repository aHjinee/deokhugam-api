package com.sbproject.deokhugam.dashboard.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularBooksRankingResponse {

	private String bookId;
	private String title;
	private String author;
	private String thumbnailUrl;
	private PeriodType period;
	private int rank;
	private double score;
	private int reviewCount;
	private double rating;
	private Instant createdAt;

	@JsonProperty("id")
	public String getId() {
		return bookId;
	}

	public static PopularBooksRankingResponse from(
		PopularBooksDocument.Ranking ranking,
		PopularBooksDocument document
	) {
		return new PopularBooksRankingResponse(
			ranking.getBookId(),
			ranking.getTitle(),
			ranking.getAuthor(),
			ranking.getThumbnailUrl(),
			document.getPeriodType(),
			ranking.getRank(),
			ranking.getScore(),
			ranking.getReviewCount(),
			ranking.getAvgRating(),
			document.getCreatedAt()
		);
	}
}