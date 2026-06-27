package com.sbproject.deokhugam.book.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.sbproject.deokhugam.book.dto.BookCreateRequest;
import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.CursorPageResponseBookDto;
import com.sbproject.deokhugam.book.dto.Direction;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;

import jakarta.validation.Valid;

public interface BookService {
	SlicePageResponse<BookDto> searchBooks(String keyword, BookOrderBy orderBy, Direction direction, String cursor,
	                                       Instant after, int limit);

	BookDto createBook(@Valid BookCreateRequest request, MultipartFile thumbnailImage);

	BookDto getBook(UUID bookId);

	String extractIsbnFromImage(MultipartFile image);
}
