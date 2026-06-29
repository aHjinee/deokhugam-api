package com.sbproject.deokhugam.review.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class ReviewNotOwnedException extends ReviewException {
	public ReviewNotOwnedException() {
		super(ErrorCode.REVIEW_NOT_OWNED);
	}

	public static ReviewNotOwnedException withId(UUID reviewId, UUID userId) {
		ReviewNotOwnedException ex = new ReviewNotOwnedException();
		ex.addDetail("reviewId", reviewId);
		ex.addDetail("userId", userId);
		return ex;
	}
}
