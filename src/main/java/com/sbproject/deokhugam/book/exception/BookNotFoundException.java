package com.sbproject.deokhugam.book.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class BookNotFoundException extends BookException {
	public BookNotFoundException() {
		super(ErrorCode.BOOK_NOT_FOUND);
	}

	public static BookNotFoundException withId(Long id) {
		BookNotFoundException ex = new BookNotFoundException();
		ex.addDetail("id", id);
		return ex;
	}
}
