package com.sbproject.deokhugam.dashboard.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@NoArgsConstructor
@TypeAlias("userActivityStats")
@Document(collection = "user_activity_stats")
@CompoundIndex(
	name = "uk_user_activity_date",
	def = "{'user_id': 1, 'activity_date': 1}",
	unique = true
)
public class UserActivityStatsDocument {

  @Id
  private String id;

  @Field("user_id")
  private String userId;

  @Field("activity_date")
  private Instant activityDate;

  @Field("review_count")
  private int reviewCount;

  @Field("comment_count")
  private int commentCount;

  @Field("like_count")
  private int likeCount;

  @Field("received_comment_count")
  private int receivedCommentCount;

  @Field("received_like_count")
  private int receivedLikeCount;

  @Field("daily_power_rank")
  private Integer dailyPowerRank;

  @Field("created_at")
  private Instant createdAt;

  @Field("updated_at")
  private Instant updatedAt;
}