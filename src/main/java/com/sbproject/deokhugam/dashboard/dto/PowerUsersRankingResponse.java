package com.sbproject.deokhugam.dashboard.dto;

import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PowerUsersRankingResponse {

	private int rank;
	private String userId;
	private String nickname;
	private double score;
	private double reviewScore;
	private int likeCount;
	private int commentCount;

	public static PowerUsersRankingResponse from(
		PowerUsersDocument.Ranking ranking
	) {
		return new PowerUsersRankingResponse(
			ranking.getRank(),
			ranking.getUserId(),
			ranking.getNickname(),
			ranking.getActivityScore(),
			ranking.getReviewScore(),
			ranking.getLikeCount(),
			ranking.getCommentCount()
		);
	}
}