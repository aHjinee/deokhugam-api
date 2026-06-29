package com.sbproject.deokhugam.review.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class ReviewNotFoundException extends ReviewException {
	public ReviewNotFoundException() {
		super(ErrorCode.REVIEW_NOT_FOUND);
	}

	public static ReviewNotFoundException withId(UUID reviewId) {
		ReviewNotFoundException ex = new ReviewNotFoundException();
		ex.addDetail("reviewId", reviewId);
		return ex;
	}
}
