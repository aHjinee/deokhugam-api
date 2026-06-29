package com.sbproject.deokhugam.book.repository;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.domain.Slice;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.Direction;
import com.sbproject.deokhugam.book.entity.Book;

public interface BookQueryRepository {

	Slice<Book> searchBooks(String keyword, BookOrderBy orderBy, Direction direction, String cursor,
	                        Instant after, int limit);
}
