package com.sbproject.deokhugam.book.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class OcrProcessingException extends BookException {
	public OcrProcessingException() {
		super(ErrorCode.OCR_PROCESSING_FAILED);
	}
}
