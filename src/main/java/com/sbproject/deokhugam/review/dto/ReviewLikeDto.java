package com.sbproject.deokhugam.review.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLikeDto {

	@Schema(description = "리뷰 고유 식별자 (UUID)", example = "018f6c47-66b0-41f2-a68f-1ea2c8db6cdc")
	private UUID reviewId;

	@Schema(description = "작성자 고유 식별자 (UUID)", example = "018f6c47-96c6-62ede2538e0e-41f2-a68f")
	private UUID userId;

	@Schema(description = "좋아요 여부")
	boolean liked;
}
