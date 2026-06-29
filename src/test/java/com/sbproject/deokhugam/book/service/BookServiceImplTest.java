package com.sbproject.deokhugam.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.uuid.Generators;
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
import com.sbproject.deokhugam.book.exception.NaverBookNotFoundException;
import com.sbproject.deokhugam.book.exception.OcrProcessingException;
import com.sbproject.deokhugam.book.mapper.BookMapper;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.book.service.impl.BookServiceImpl;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.storage.FileStorage;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

	Instant createdAt;
	Instant updatedAt;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private FileStorage fileStorage;
	@Mock
	private NaverClient naverClient;
	@Mock
	private GeminiOcrClient ocrClient;
	@Mock
	private BookMapper bookMapper;
	@InjectMocks
	private BookServiceImpl bookService;
	private UUID bookId;
	private String title;
	private String author;
	private String description;
	private String publisher;
	private LocalDate publishedDate;
	private String thumbnailUrl;
	private String isbn;
	private Integer reviewCount;
	private Double rating;
	private MockMultipartFile image;
	private Book book;
	private BookDto bookDto;

	@BeforeEach
	void setUp() {
		bookId = Generators.timeBasedEpochGenerator().generate();
		title = "Clean Code(클린 코드) (애자일 소프트웨어 장인 정신)";
		author = "로버트 C. 마틴";
		description = """
			프로그래머, 소프트웨어 공학도, 프로젝트 관리자, 팀 리더, 시스템 분석가에게 도움이 될
			더 나은 코드를 만드는 책
			""";
		publisher = "인사이트";
		publishedDate = LocalDate.parse("2013-12-24");
		thumbnailUrl = "thumbnailUrl";
		isbn = "9788966260959";
		reviewCount = 0;
		rating = 0.0;
		createdAt = Instant.now();
		updatedAt = Instant.now();

		image = new MockMultipartFile("thumbnailImage", "cover.jpg", "image/jpeg", new byte[] {1, 2, 3});
		book = Book.builder()
		           .id(bookId)
		           .isbn(isbn)
		           .title(title)
		           .author(author)
		           .description(description)
		           .publisher(publisher)
		           .publishedDate(publishedDate)
		           .thumbnailUrl(thumbnailUrl)
		           .reviewCount(reviewCount)
		           .totalScore(0)
		           .rating(rating)
		           .build();

		bookDto = new BookDto(bookId, title, author, description, publisher, publishedDate, isbn, thumbnailUrl,
		                      reviewCount, rating, createdAt, updatedAt);
	}

	@Test
	@DisplayName("도서 목록 조회 성공 - 키워드 검색 결과가 있으면 정보를 채워 반환")
	void searchBooks_keyword() {
		//given
		String keyword = "코드";
		BookOrderBy orderBy = BookOrderBy.title;
		Direction direction = Direction.DESC;
		String cursor = null;
		Instant after = null;
		int limit = 50;
		Slice<Book> slice = new SliceImpl<>(List.of(book), PageRequest.of(0, limit), false);
		given(bookRepository.countByKeyword(keyword)).willReturn(1L);
		given(bookRepository.searchBooks(keyword, orderBy, direction, cursor, after, limit)).willReturn(slice);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		SlicePageResponse<BookDto> response =
			bookService.searchBooks(keyword, orderBy, direction, cursor, after, limit);

		//then
		assertThat(response.getContent()).containsExactly(bookDto);
		assertThat(response.getNextCursor()).isEqualTo(title);
		assertThat(response.getNextAfter()).isEqualTo(bookDto.createdAt());
		assertThat(response.getSize()).isEqualTo(1);
		assertThat(response.getTotalElements()).isEqualTo(1L);
	}

	@Test
	@DisplayName("도서 목록 조회 - 결과가 없으면 빈 응답을 반환")
	void searchBooks_emptyResult() {
		//given
		String keyword = "존재하지않는키워드";
		BookOrderBy orderBy = BookOrderBy.title;
		Direction direction = Direction.DESC;
		int limit = 50;
		Slice<Book> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, limit), false);
		given(bookRepository.countByKeyword(keyword)).willReturn(0L);
		given(bookRepository.searchBooks(keyword, orderBy, direction, null, null, limit)).willReturn(emptySlice);

		//when
		SlicePageResponse<BookDto> response =
			bookService.searchBooks(keyword, orderBy, direction, null, null, limit);

		//then
		assertThat(response.getContent()).isEmpty();
		assertThat(response.getNextCursor()).isNull();
		assertThat(response.getNextAfter()).isNull();
		assertThat(response.getSize()).isZero();
		assertThat(response.getTotalElements()).isZero();
		assertThat(response.isHasNext()).isFalse();
	}

	@Test
	@DisplayName("도서 목록 조회 - 전체 목록 조회")
	void searchBooks_noKeyword_usesTotalCount() {
		//given
		BookOrderBy orderBy = BookOrderBy.rating;
		Direction direction = Direction.DESC;
		int limit = 50;
		Slice<Book> slice = new SliceImpl<>(List.of(book), PageRequest.of(0, limit), false);
		given(bookRepository.count()).willReturn(1L);
		given(bookRepository.searchBooks(null, orderBy, direction, null, null, limit)).willReturn(slice);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		SlicePageResponse<BookDto> response =
			bookService.searchBooks(null, orderBy, direction, null, null, limit);

		//then
		assertThat(response.getContent()).containsExactly(bookDto);
		assertThat(response.getNextCursor()).isEqualTo(String.valueOf(bookDto.rating()));
		assertThat(response.getTotalElements()).isEqualTo(1L);
	}

	@Test
	@DisplayName("도서 목록 조회 - 키워드가 공백이면 전체 개수를 세고 publishedDate 커서를 만든다")
	void searchBooks_blankKeyword_orderByPublishedDate() {
		//given
		String keyword = "   ";
		BookOrderBy orderBy = BookOrderBy.publishedDate;
		Direction direction = Direction.DESC;
		int limit = 50;
		Slice<Book> slice = new SliceImpl<>(List.of(book), PageRequest.of(0, limit), false);
		given(bookRepository.count()).willReturn(1L);
		given(bookRepository.searchBooks(keyword, orderBy, direction, null, null, limit)).willReturn(slice);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		SlicePageResponse<BookDto> response =
			bookService.searchBooks(keyword, orderBy, direction, null, null, limit);

		//then
		assertThat(response.getNextCursor()).isEqualTo(String.valueOf(bookDto.publishedDate()));
		assertThat(response.getTotalElements()).isEqualTo(1L);
	}

	@Test
	@DisplayName("도서 목록 조회 - reviewCount 정렬이면 reviewCount 커서를 만든다")
	void searchBooks_orderByReviewCount() {
		//given
		String keyword = "코드";
		BookOrderBy orderBy = BookOrderBy.reviewCount;
		Direction direction = Direction.DESC;
		int limit = 50;
		Slice<Book> slice = new SliceImpl<>(List.of(book), PageRequest.of(0, limit), false);
		given(bookRepository.countByKeyword(keyword)).willReturn(1L);
		given(bookRepository.searchBooks(keyword, orderBy, direction, null, null, limit)).willReturn(slice);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		SlicePageResponse<BookDto> response =
			bookService.searchBooks(keyword, orderBy, direction, null, null, limit);

		//then
		assertThat(response.getNextCursor()).isEqualTo(String.valueOf(bookDto.reviewCount()));
	}

	@Test
	@DisplayName("도서 등록 - 신규 ISBN이면 새로 저장")
	void createBook_success() {
		//given
		BookCreateRequest request =
			new BookCreateRequest(title, author, description, publisher, publishedDate, isbn);
		given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());
		given(bookRepository.save(any(Book.class))).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.createBook(request, null);

		//then
		assertThat(response).isEqualTo(bookDto);
		then(bookRepository).should().save(any(Book.class));
	}

	@Test
	@DisplayName("도서 등록 - 이미 존재하는 ISBN이면 BookAlreadyExistsException")
	void createBook_fail_duplicateIsbn() {
		//given
		BookCreateRequest request =
			new BookCreateRequest(title, author, description, publisher, publishedDate, isbn);
		given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(book)); // 삭제되지 않은 기존 도서

		//when & then
		assertThatThrownBy(() -> bookService.createBook(request, null))
			.isInstanceOf(BookAlreadyExistsException.class);
		then(bookRepository).should(never()).save(any(Book.class));
	}

	@Test
	@DisplayName("도서 등록 - 논리 삭제된 ISBN이면 기존 도서를 갱신해 재등록")
	void createBook_reRegisterSoftDeleted() {
		//given
		BookCreateRequest request =
			new BookCreateRequest(title, author, description, publisher, publishedDate, isbn);
		book.markDeleted(); // 논리 삭제 상태
		given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(book));
		given(bookRepository.save(book)).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.createBook(request, null);

		//then
		assertThat(response).isEqualTo(bookDto);
		assertThat(book.isDeleted()).isFalse(); // update()에서 deletedAt이 초기화됨
	}

	@Test
	@DisplayName("도서 등록 - 썸네일 이미지가 있으면 저장 후 URL을 사용")
	void createBook_withThumbnail() {
		//given
		BookCreateRequest request =
			new BookCreateRequest(title, author, description, publisher, publishedDate, isbn);
		given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());
		given(fileStorage.save(image)).willReturn("storageKey");
		given(fileStorage.getUrl("storageKey")).willReturn("http://localhost:8080/files/storageKey");
		given(bookRepository.save(any(Book.class))).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.createBook(request, image);

		//then
		assertThat(response).isEqualTo(bookDto);
		then(fileStorage).should().save(image);
		then(fileStorage).should().getUrl("storageKey");
	}

	@Test
	@DisplayName("도서 등록 - 썸네일이 비어 있으면 저장소를 호출하지 않는다")
	void createBook_emptyThumbnail() {
		//given
		BookCreateRequest request =
			new BookCreateRequest(title, author, description, publisher, publishedDate, isbn);
		MockMultipartFile emptyImage = new MockMultipartFile("thumbnailImage", new byte[0]);
		given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());
		given(bookRepository.save(any(Book.class))).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.createBook(request, emptyImage);

		//then
		assertThat(response).isEqualTo(bookDto);
		then(fileStorage).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("도서 상세 조회 - 성공")
	void getBook_success() {
		//given
		given(bookRepository.findByIdAndDeletedAtIsNull(bookId)).willReturn(Optional.of(book));
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.getBook(bookId);

		//then
		assertThat(response).isEqualTo(bookDto);
	}

	@Test
	@DisplayName("도서 상세 조회 - 실패")
	void getBook_fail() {
		//given
		given(bookRepository.findByIdAndDeletedAtIsNull(bookId)).willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> bookService.getBook(bookId))
			.isInstanceOf(BookNotFoundException.class);
	}

	@Test
	@DisplayName("ISBN 이미지 추출 - OCR 클라이언트 결과를 그대로 반환")
	void extractIsbnFromImage_success() {
		//given
		given(ocrClient.extractIsbn(image)).willReturn(isbn);

		//when
		String result = bookService.extractIsbnFromImage(image);

		//then
		assertThat(result).isEqualTo(isbn);
		then(ocrClient).should().extractIsbn(image);
	}

	@Test
	@DisplayName("ISBN 이미지 추출 - OCR 처리 실패 시 OcrProcessingException")
	void extractIsbnFromImage_fail() {
		//given
		given(ocrClient.extractIsbn(image)).willThrow(new OcrProcessingException());

		//when & then
		assertThatThrownBy(() -> bookService.extractIsbnFromImage(image))
			.isInstanceOf(OcrProcessingException.class);
	}

	@Test
	@DisplayName("도서 논리 삭제 - 성공하면 deletedAt이 설정")
	void deleteBook_success() {
		//given
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

		//when
		bookService.deleteBook(bookId);

		//then
		assertThat(book.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("도서 논리 삭제 - 존재하지 않으면 BookNotFoundException")
	void deleteBook_fail_notFound() {
		//given
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> bookService.deleteBook(bookId))
			.isInstanceOf(BookNotFoundException.class);
	}

	@Test
	@DisplayName("도서 수정 - 썸네일 없이 메타데이터만 수정하면 기존 썸네일을 유지")
	void updateBook_withoutThumbnail() {
		//given
		BookUpdateRequest request = new BookUpdateRequest(
			"리팩터링 2판", "마틴 파울러", "코드 구조 개선", "한빛미디어", LocalDate.parse("2020-04-01"));
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		given(bookRepository.save(book)).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.updateBook(bookId, request, null);

		//then
		assertThat(response).isEqualTo(bookDto);
		assertThat(book.getTitle()).isEqualTo("리팩터링 2판");
		assertThat(book.getThumbnailUrl()).isEqualTo(thumbnailUrl);
		then(fileStorage).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("도서 수정 - 썸네일 이미지가 있으면 저장 후 URL로 교체")
	void updateBook_withThumbnail() {
		//given
		BookUpdateRequest request =
			new BookUpdateRequest(title, author, description, publisher, publishedDate);
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		given(fileStorage.save(image)).willReturn("storageKey");
		given(fileStorage.getUrl("storageKey")).willReturn("http://localhost:8080/files/storageKey");
		given(bookRepository.save(book)).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.updateBook(bookId, request, image);

		//then
		assertThat(response).isEqualTo(bookDto);
		assertThat(book.getThumbnailUrl()).isEqualTo("http://localhost:8080/files/storageKey");
		then(fileStorage).should().save(image);
		then(fileStorage).should().getUrl("storageKey");
	}

	@Test
	@DisplayName("도서 수정 - 존재하지 않으면 BookNotFoundException")
	void updateBook_fail_notFound() {
		//given
		BookUpdateRequest request =
			new BookUpdateRequest(title, author, description, publisher, publishedDate);
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> bookService.updateBook(bookId, request, null))
			.isInstanceOf(BookNotFoundException.class);
		then(bookRepository).should(never()).save(any(Book.class));
	}

	@Test
	@DisplayName("도서 수정 - 썸네일이 비어 있으면 저장소를 호출하지 않고 기존 썸네일을 유지한다")
	void updateBook_emptyThumbnail() {
		//given
		BookUpdateRequest request =
			new BookUpdateRequest(title, author, description, publisher, publishedDate);
		MockMultipartFile emptyImage = new MockMultipartFile("thumbnailImage", new byte[0]); // isEmpty() == true
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
		given(bookRepository.save(book)).willReturn(book);
		given(bookMapper.toBookDto(book)).willReturn(bookDto);

		//when
		BookDto response = bookService.updateBook(bookId, request, emptyImage);

		//then
		assertThat(response).isEqualTo(bookDto);
		assertThat(book.getThumbnailUrl()).isEqualTo(thumbnailUrl); // 기존 썸네일 유지
		then(fileStorage).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("네이버 도서 정보 조회 - NaverClient 결과를 그대로 반환")
	void getBookInfo_success() {
		//given
		NaverBookDto naverBookDto =
			new NaverBookDto(title, author, description, publisher, publishedDate, isbn, "base64image");
		given(naverClient.getBookInfo(isbn)).willReturn(naverBookDto);

		//when
		NaverBookDto response = bookService.getBookInfo(isbn);

		//then
		assertThat(response).isEqualTo(naverBookDto);
		then(naverClient).should().getBookInfo(isbn);
	}

	@Test
	@DisplayName("네이버 도서 정보 조회 - 결과가 없으면 NaverBookNotFoundException")
	void getBookInfo_fail_notFound() {
		//given
		given(naverClient.getBookInfo(isbn)).willThrow(NaverBookNotFoundException.withIsbn(isbn));

		//when & then
		assertThatThrownBy(() -> bookService.getBookInfo(isbn))
			.isInstanceOf(NaverBookNotFoundException.class);
	}

	@Test
	@DisplayName("도서 물리 삭제 - 성공하면 레코드를 삭제")
	void hardDeleteBook_success() {
		//given
		given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

		//when
		bookService.hardDeleteBook(bookId);

		//then
		then(bookRepository).should().delete(book);
	}

	@Test
	@DisplayName("도서 물리 삭제 - 존재하지 않으면 BookNotFoundException")
	void hardDeleteBook_fail_notFound() {
		//given
		given(bookRepository.findById(bookId)).willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> bookService.hardDeleteBook(bookId))
			.isInstanceOf(BookNotFoundException.class);
		then(bookRepository).should(never()).delete(any(Book.class));
	}
}
