package com.sbproject.deokhugam.book.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.CursorPageResponseBookDto;
import com.sbproject.deokhugam.book.dto.Direction;
import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.exception.BookNotFoundException;
import com.sbproject.deokhugam.book.mapper.BookMapper;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.book.service.BookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Override
	public CursorPageResponseBookDto searchBooks(String keyword, BookOrderBy orderBy, Direction direction, String cursor,
	                                           Instant after, int limit) {
		Long totalElements = (keyword == null || keyword.isBlank())
			? bookRepository.count()
			: bookRepository.countByKeyword(keyword);

		Slice<Book> books = bookRepository.searchBooks(keyword, orderBy, direction, cursor, after, limit);
		int size = books.getContent().size();
		List<BookDto> bookDtoList = books.stream()
		                                .map(bookMapper::toBookDto)
		                                .toList();
		if (bookDtoList.isEmpty()) {
			return new CursorPageResponseBookDto(List.of(), null, null, 0, totalElements, false);
		}
		BookDto lastBook = bookDtoList.get(size - 1);
		String nextCursor = switch (orderBy) {
			case title -> lastBook.title();
			case publishedDate -> String.valueOf(lastBook.publishedDate());
			case rating -> String.valueOf(lastBook.rating());
			case reviewCount -> String.valueOf(lastBook.reviewCount());
		};

		return new CursorPageResponseBookDto(bookDtoList, nextCursor, lastBook.createdAt(),
											 size, totalElements, books.hasNext());
	}

	@Override
	public BookDto getBook(UUID bookId) {
		Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);

		return bookMapper.toBookDto(book);
	}
}
