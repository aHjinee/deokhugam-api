package com.sbproject.deokhugam.book.dto;

public record NaverBookItem(
	String title,
	String link,
	String image,
	String author,
	String discount,
	String publisher,
	String pubdate,
	String isbn,
	String description
) {
}
