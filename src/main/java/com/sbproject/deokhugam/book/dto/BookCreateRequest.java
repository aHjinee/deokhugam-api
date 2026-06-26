package com.sbproject.deokhugam.book.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.ISBN;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;

public record BookCreateRequest(

	@Size(min = 1, max = 50)
	String title,

	@Size(min = 1, max = 255)
	String author,

	@Size(min = 1, max = 255)
	String description,

	@Size(min = 1, max = 100)
	String publisher,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate publishedDate,

	@ISBN
	String isbn
) {
}
