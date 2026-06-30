package com.sbproject.deokhugam.book.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookUpdateRequest(
	@NotBlank @Size(min = 1, max = 255)
	String title,

	@NotBlank @Size(min = 1, max = 255)
	String author,

	@NotBlank @Size(min = 1)
	String description,

	@NotBlank @Size(min = 1, max = 100)
	String publisher,

	@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate publishedDate
) {
}
