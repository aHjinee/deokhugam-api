package com.sbproject.deokhugam.dashboard.dto;

import java.time.Instant;

import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PowerUsersRankingResponse {

	private int rank;
	private String userId;
	private String nickname;
	private PeriodType period;
	private Instant createdAt;
	private double score;
	private double reviewScoreSum;
	private int likeCount;
	private int commentCount;

	public static PowerUsersRankingResponse from(
		PowerUsersDocument.Ranking ranking,
		PowerUsersDocument document
	) {
		return new PowerUsersRankingResponse(
			ranking.getRank(),
			ranking.getUserId(),
			ranking.getNickname(),
			document.getPeriodType(),
			document.getCreatedAt(),
			ranking.getActivityScore(),
			ranking.getReviewScore(),
			ranking.getLikeCount(),
			ranking.getCommentCount()
		);
	}
}