package com.sbproject.deokhugam.book.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sbproject.deokhugam.book.client.GeminiOcrClient;
import com.sbproject.deokhugam.book.client.NaverClient;
import com.sbproject.deokhugam.book.dto.BookCreateRequest;
import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.BookUpdateRequest;
import com.sbproject.deokhugam.book.dto.Direction;
import com.sbproject.deokhugam.book.dto.NaverBookDto;
import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.exception.BookAlreadyExistsException;
import com.sbproject.deokhugam.book.exception.BookNotFoundException;
import com.sbproject.deokhugam.book.mapper.BookMapper;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.book.service.BookService;
import com.sbproject.deokhugam.book.service.ImageFileValidator;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.storage.FileStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final BookMapper bookMapper;
	private final FileStorage fileStorage;
	private final NaverClient naverClient;
	private final GeminiOcrClient ocrClient;
	private final ImageFileValidator imageFileValidator;

	@Override
	public SlicePageResponse<BookDto> searchBooks(String keyword, BookOrderBy orderBy, Direction direction,
	                                              String cursor, Instant after, int limit) {
		long totalElements = (keyword == null || keyword.isBlank())
			? bookRepository.count()
			: bookRepository.countByKeyword(keyword);

		Slice<Book> books = bookRepository.searchBooks(keyword, orderBy, direction, cursor, after, limit);
		int size = books.getContent().size();
		List<BookDto> bookDtoList = books.stream()
		                                 .map(bookMapper::toBookDto)
		                                 .toList();
		if (bookDtoList.isEmpty()) {
			return new SlicePageResponse<>(List.of(), null, null, 0, totalElements, false);
		}
		BookDto lastBook = bookDtoList.get(size - 1);
		String nextCursor = switch (orderBy) {
			case title -> lastBook.title();
			case publishedDate -> String.valueOf(lastBook.publishedDate());
			case rating -> String.valueOf(lastBook.rating());
			case reviewCount -> String.valueOf(lastBook.reviewCount());
		};

		return new SlicePageResponse<>(bookDtoList, nextCursor, lastBook.createdAt(),
		                               size, totalElements, books.hasNext());
	}

	@Override
	@Transactional
	public BookDto createBook(@NonNull BookCreateRequest request, MultipartFile thumbnailImage) {
		Optional<Book> book = bookRepository.findByIsbn(request.isbn());
		String imageUrl = null;
		if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
			imageFileValidator.validate(thumbnailImage);
			String storageKey = fileStorage.save(thumbnailImage);
			imageUrl = fileStorage.getUrl(storageKey);
		}
		if (book.isPresent()) {
			if (book.get().isDeleted()) {
				book.get().update(
					request.title(),
					request.author(),
					request.description(),
					request.publisher(),
					request.publishedDate(),
					imageUrl
				);
				Book savedBook = bookRepository.save(book.get());
				return bookMapper.toBookDto(savedBook);
			} else {
				throw BookAlreadyExistsException.withIsbn(request.isbn());
			}
		} else {
			Book createBook = Book.builder()
			                      .isbn(request.isbn())
			                      .title(request.title())
			                      .author(request.author())
			                      .description(request.description())
			                      .publisher(request.publisher())
			                      .publishedDate(request.publishedDate())
			                      .thumbnailUrl(imageUrl)
			                      .reviewCount(0)
			                      .totalScore(0)
			                      .rating(0.0)
			                      .build();
			Book savedBook = bookRepository.save(createBook);
			return bookMapper.toBookDto(savedBook);
		}
	}

	@Override
	public BookDto getBook(UUID bookId) {
		Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
		                          .orElseThrow(() -> BookNotFoundException.withId(bookId));

		return bookMapper.toBookDto(book);
	}

	@Override
	public String extractIsbnFromImage(MultipartFile image) {
		imageFileValidator.validate(image);
		return ocrClient.extractIsbn(image);
	}

	@Override
	@Transactional
	public void deleteBook(UUID bookId) {
		Book book = getBookOrThrow(bookId);
		book.markDeleted();
	}

	@Override
	@Transactional
	public BookDto updateBook(UUID bookId, BookUpdateRequest request, MultipartFile thumbnailImage) {
		Book book = getBookOrThrow(bookId);
		String imageUrl = book.getThumbnailUrl();
		if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
			imageFileValidator.validate(thumbnailImage);
			String storageKey = fileStorage.save(thumbnailImage);
			imageUrl = fileStorage.getUrl(storageKey);
		}
		book.update(request.title(), request.author(), request.description(), request.publisher(),
		            request.publishedDate(), imageUrl);
		return bookMapper.toBookDto(bookRepository.save(book));
	}

	@Override
	public NaverBookDto getBookInfo(String isbn) {
		return naverClient.getBookInfo(isbn);
	}

	@Override
	@Transactional
	public void hardDeleteBook(UUID bookId) {
		Book book = getBookOrThrow(bookId);
		bookRepository.delete(book);
	}

	private Book getBookOrThrow(UUID bookId){
		return bookRepository.findById(bookId).orElseThrow(() -> BookNotFoundException.withId(bookId));
	}


}
