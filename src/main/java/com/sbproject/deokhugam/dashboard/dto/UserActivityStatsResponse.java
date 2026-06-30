package com.sbproject.deokhugam.dashboard.dto;

import com.sbproject.deokhugam.dashboard.document.UserActivityStatsDocument;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class UserActivityStatsResponse {

	private final String userId;
	private final List<UserActivityStatEntry> content;

	public UserActivityStatsResponse(
		String userId,
		List<UserActivityStatEntry> content
	) {
		this.userId = userId;
		this.content = content;
	}

	public static UserActivityStatsResponse empty(String userId) {
		return new UserActivityStatsResponse(userId, List.of());
	}

	@Getter
	public static class UserActivityStatEntry {

		private final Instant activityDate;
		private final int reviewCount;
		private final int commentCount;
		private final int likeCount;
		private final int receivedCommentCount;
		private final int receivedLikeCount;

		// 순위가 아직 확정되지 않은 경우 null
		private final Integer dailyPowerRank;

		private UserActivityStatEntry(UserActivityStatsDocument doc) {
			this.activityDate = doc.getActivityDate();
			this.reviewCount = doc.getReviewCount();
			this.commentCount = doc.getCommentCount();
			this.likeCount = doc.getLikeCount();
			this.receivedCommentCount = doc.getReceivedCommentCount();
			this.receivedLikeCount = doc.getReceivedLikeCount();
			this.dailyPowerRank = doc.getDailyPowerRank();
		}

		public static UserActivityStatEntry from(
			UserActivityStatsDocument doc
		) {
			return new UserActivityStatEntry(doc);
		}
	}
}