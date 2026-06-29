package com.sbproject.deokhugam.book.dto;

public enum IsbnExtractionFailReason {
	NOT_BOOK_IMAGE,
	NO_ISBN,
	BLURRY_OR_DAMAGED,
	BARCODE_ONLY_NO_NUMBER,
	MULTIPLE_ISBN_AMBIGUOUS,
	INVALID_LENGTH,
	UNKNOWN
}
