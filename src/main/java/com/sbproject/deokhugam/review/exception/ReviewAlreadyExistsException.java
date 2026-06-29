package com.sbproject.deokhugam.review.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class ReviewAlreadyExistsException extends ReviewException {
	public ReviewAlreadyExistsException() {
		super(ErrorCode.REVIEW_NOT_OWNED);
	}

	public static ReviewAlreadyExistsException withIds(UUID userId, UUID bookId) {
		ReviewAlreadyExistsException ex = new ReviewAlreadyExistsException();
		ex.addDetail("userId", userId);
		ex.addDetail("bookId", bookId);
		return ex;
	}
}
