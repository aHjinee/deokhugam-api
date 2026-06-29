package com.sbproject.deokhugam.book.exception;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class BookException extends BaseException {
	public BookException(ErrorCode errorCode) {
		super(errorCode);
	}
	public BookException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}