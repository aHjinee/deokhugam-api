package com.sbproject.deokhugam.comments.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class ReviewNotFoundException extends BaseException {

	public ReviewNotFoundException(UUID reviewId) {
		super(ErrorCode.POST_NOT_FOUND);
		addDetail("reviewId", reviewId);
	}
}
