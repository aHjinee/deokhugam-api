package com.sbproject.deokhugam.book.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record NaverBookDto(
	String title,
	String author,
	String description,
	String publisher,
	LocalDate publishedDate,
	String isbn,
	String thumbnailImage
) {
	public NaverBookDto(NaverBookItem item, String image){
		this(
			item.title(),
			item.author(),
			item.description(),
			item.publisher(),
			LocalDate.parse(item.pubdate(), DateTimeFormatter.ofPattern("yyyyMMdd")),
			item.isbn(),
			image
		);
	}
}
