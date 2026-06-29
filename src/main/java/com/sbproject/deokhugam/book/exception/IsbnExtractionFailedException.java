package com.sbproject.deokhugam.book.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class IsbnExtractionFailedException extends BookException {
	public IsbnExtractionFailedException() {
		super(ErrorCode.ISBN_EXTRACTION_FAILED);
	}

	public static IsbnExtractionFailedException withReason(String reason) {
		IsbnExtractionFailedException ex = new IsbnExtractionFailedException();
		ex.addDetail("reason", reason);
		return ex;
	}
}
