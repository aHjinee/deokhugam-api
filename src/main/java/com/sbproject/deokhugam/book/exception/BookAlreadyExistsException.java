package com.sbproject.deokhugam.book.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class BookAlreadyExistsException extends BookException {
	public BookAlreadyExistsException() {
		super(ErrorCode.BOOK_ALREADY_EXISTS);
	}

	public static BookAlreadyExistsException withIsbn(String isbn) {
		BookAlreadyExistsException ex = new BookAlreadyExistsException();
		ex.addDetail("isbn", isbn);
		return ex;
	}
}
