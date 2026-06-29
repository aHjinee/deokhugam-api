package com.sbproject.deokhugam.book.dto;

public record IsbnResponseDto(
	String analysis,
	boolean found,
	String isbn,
	String reason
) {}