package com.sbproject.deokhugam.book.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.CursorPageResponseBookDto;
import com.sbproject.deokhugam.book.dto.Direction;
import com.sbproject.deokhugam.book.service.BookService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;

	@GetMapping()
	public ResponseEntity<CursorPageResponseBookDto> getBookList(
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "title") BookOrderBy orderBy,
		@RequestParam(defaultValue = "DESC") Direction direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) Instant after,
		@RequestParam(defaultValue = "50") @Min(1) int limit
	) {
		log.info("keyword: {}, orderBy: {}, direction: {}, cursor: {}, after: {}, limit: {}", keyword, orderBy, direction, cursor, after, limit);
		return ResponseEntity.ok(bookService.searchBooks(keyword, orderBy, direction, cursor, after, limit));
	}


}
