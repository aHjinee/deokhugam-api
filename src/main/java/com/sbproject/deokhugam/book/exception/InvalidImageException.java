package com.sbproject.deokhugam.book.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class InvalidImageException extends BookException {
	public InvalidImageException() {
		super(ErrorCode.INVALID_IMAGE_FILE);
	}

	public static InvalidImageException withReason(String reason) {
		InvalidImageException ex = new InvalidImageException();
		ex.addDetail("reason", reason);
		return ex;
	}
}
