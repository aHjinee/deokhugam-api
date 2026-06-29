package com.sbproject.deokhugam.book.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class BookNotFoundException extends BookException {
	public BookNotFoundException() {
		super(ErrorCode.BOOK_NOT_FOUND);
	}

	public static BookNotFoundException withId(UUID id) {
		BookNotFoundException ex = new BookNotFoundException();
		ex.addDetail("id", id);
		return ex;
	}
}
