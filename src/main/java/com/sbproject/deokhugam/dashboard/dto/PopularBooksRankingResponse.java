package com.sbproject.deokhugam.dashboard.dto;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularBooksRankingResponse {

	private int rank;
	private String bookId;
	private String title;
	private String author;
	private String thumbnailUrl;
	private double score;
	private int reviewCount;
	private double rating;

	public static PopularBooksRankingResponse from(
		PopularBooksDocument.Ranking ranking
	) {
		return new PopularBooksRankingResponse(
			ranking.getRank(),
			ranking.getBookId(),
			ranking.getTitle(),
			ranking.getAuthor(),
			ranking.getThumbnailUrl(),
			ranking.getScore(),
			ranking.getReviewCount(),
			ranking.getAvgRating()
		);
	}
}