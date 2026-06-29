package com.sbproject.deokhugam.book.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class NaverBookNotFoundException extends BookException {
	public NaverBookNotFoundException() {
		super(ErrorCode.NAVER_BOOK_NOT_FOUND);
	}

	public static NaverBookNotFoundException withIsbn(String isbn) {
		NaverBookNotFoundException ex = new NaverBookNotFoundException();
		ex.addDetail("isbn", isbn);
		return ex;
	}
}
