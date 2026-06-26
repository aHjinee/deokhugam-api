package com.sbproject.deokhugam.book.service;

import java.time.Instant;
import java.util.UUID;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.CursorPageResponseBookDto;
import com.sbproject.deokhugam.book.dto.Direction;

public interface BookService {
	CursorPageResponseBookDto searchBooks(String keyword, BookOrderBy orderBy, Direction direction, String cursor,
	                                    Instant after, int limit);

}
