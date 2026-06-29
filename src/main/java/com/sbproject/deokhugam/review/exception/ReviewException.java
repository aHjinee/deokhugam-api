package com.sbproject.deokhugam.review.exception;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class ReviewException extends BaseException {
	public ReviewException(ErrorCode errorCode) {
		super(errorCode);
	}
	public ReviewException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}
