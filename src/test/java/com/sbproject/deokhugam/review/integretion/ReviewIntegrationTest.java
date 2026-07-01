package com.sbproject.deokhugam.review.integretion;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.review.dto.ReviewCreateRequest;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewUpdateRequest;
import com.sbproject.deokhugam.review.service.ReviewService;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReviewIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private UserRepository userRepository;

	private Book testBook;
	private User testUser;
	private final String HEADER_USER_ID = "Deokhugam-Request-User-ID";

	@BeforeEach
	void setUp() {
		testBook = Book.builder()
			.isbn("9788960777330") // 올바른 형식의 유효한 13자리 ISBN
			.title("자바 ORM 표준 JPA 프로그래밍")
			.author("김영한")
			.description("JPA 원리부터 실무 고도화 전략까지 총망라한 기본서입니다.")
			.publisher("에이콘출판사")
			.publishedDate(LocalDate.of(2015, 7, 28))
			.thumbnailUrl("https://example.com/thumbnails/book123.png")
			.reviewCount(0)
			.totalScore(0)
			.rating(0.0)
			.build();
		testBook = bookRepository.save(testBook);

		testUser = User.builder()
			.email("testuser@example.com")
			.nickname("덕후감빌더") // 2자 이상 50자 이하 조건 만족
			.password("$2a$10$dXJ3...[mock_hashed_password]")
			.build();
		testUser = userRepository.save(testUser);
	}

	@Test
	@DisplayName("리뷰 생성 API 통합 테스트")
	void createReview_Success() throws Exception {
		// Given
		ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
			.bookId(testBook.getId())
			.userId(testUser.getId())
			.content("JPA의 프록시와 지연 로딩 원리를 이해하는 데 최고의 책입니다!")
			.rating(5)
			.build();

		String requestBody = objectMapper.writeValueAsString(createRequest);

		// When & Then
		mockMvc.perform(post("/api/reviews")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.bookId", is(testBook.getId().toString())))
			.andExpect(jsonPath("$.bookTitle", is("자바 ORM 표준 JPA 프로그래밍")))
			.andExpect(jsonPath("$.userId", is(testUser.getId().toString())))
			.andExpect(jsonPath("$.userNickname", is("덕후감빌더")))
			.andExpect(jsonPath("$.content", is("JPA의 프록시와 지연 로딩 원리를 이해하는 데 최고의 책입니다!")))
			.andExpect(jsonPath("$.rating", is(5)))
			.andDo(print());
	}

	@Test
	@DisplayName("리뷰 생성 실패 API 통합 테스트 - 유효하지 않은 요청 데이터 검증")
	void createReview_Failure_InvalidRequest() throws Exception {
		// Given
		ReviewCreateRequest invalidRequest = ReviewCreateRequest.builder()
			.bookId(null) // @NotNull 조건 위반
			.userId(testUser.getId())
			.content("") // @NotBlank 및 @Size(min=1) 조건 위반
			.rating(10) // @Max(5) 조건 위반
			.build();

		String requestBody = objectMapper.writeValueAsString(invalidRequest);

		// When & Then
		mockMvc.perform(post("/api/reviews")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	@Test
	@DisplayName("리뷰 상세 정보 조회 API 통합 테스트")
	void getReviewDetail_Success() throws Exception {
		// Given
		ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
			.bookId(testBook.getId())
			.userId(testUser.getId())
			.content("통합 테스트 조회용 본문 데이터입니다.")
			.rating(4)
			.build();
		ReviewDto createdReview = reviewService.create(createRequest);

		// When & Then
		mockMvc.perform(get("/api/reviews/{reviewId}", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(createdReview.getId().toString())))
			.andExpect(jsonPath("$.content", is("통합 테스트 조회용 본문 데이터입니다.")))
			.andExpect(jsonPath("$.rating", is(4)))
			.andDo(print());
	}

	@Test
	@DisplayName("리뷰 수정 API 통합 테스트")
	void updateReview_Success() throws Exception {
		// Given
		ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
			.bookId(testBook.getId())
			.userId(testUser.getId())
			.content("수정 전 초기 본문 데이터")
			.rating(3)
			.build();
		ReviewDto createdReview = reviewService.create(createRequest);

		ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
			.content("완벽하게 업데이트 완료된 수정 본문 데이터")
			.rating(5)
			.build();
		String requestBody = objectMapper.writeValueAsString(updateRequest);

		// When & Then
		mockMvc.perform(patch("/api/reviews/{reviewId}", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(createdReview.getId().toString())))
			.andExpect(jsonPath("$.content", is("완벽하게 업데이트 완료된 수정 본문 데이터")))
			.andExpect(jsonPath("$.rating", is(5)))
			.andDo(print());
	}

	@Test
	@DisplayName("리뷰 논리 삭제 및 확인 API 통합 테스트")
	void deleteReviewLogical_Success() throws Exception {
		// Given
		ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
			.bookId(testBook.getId())
			.userId(testUser.getId())
			.content("소프트 삭제될 타겟 리뷰 본문입니다.")
			.rating(4)
			.build();
		ReviewDto createdReview = reviewService.create(createRequest);

		// When & Then (소프트 삭제 처리 요청)
		mockMvc.perform(delete("/api/reviews/{reviewId}", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString()))
			.andExpect(status().isNoContent());

		// 삭제 확인 - 서비스 스펙 상 삭제 플래그(deletedAt) 감지 시 ReviewNotFoundException(404)이 발생하는지 교차 검증
		mockMvc.perform(get("/api/reviews/{reviewId}", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString()))
			.andExpect(status().isNotFound())
			.andDo(print());
	}

	@Test
	@DisplayName("리뷰 물리 삭제 API 통합 테스트")
	void deleteReviewPhysical_Success() throws Exception {
		// Given
		ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
			.bookId(testBook.getId())
			.userId(testUser.getId())
			.content("DB에서 제거될 영구 제거용 리뷰 본문입니다.")
			.rating(2)
			.build();
		ReviewDto createdReview = reviewService.create(createRequest);

		// When & Then (물리 삭제 처리 요청)
		mockMvc.perform(delete("/api/reviews/{reviewId}/hard", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString()))
			.andExpect(status().isNoContent());

		// 삭제 확인 - 영구 소멸 후 단건 조회 시 데이터 부재로 404 차단 응답 확인
		mockMvc.perform(get("/api/reviews/{reviewId}", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString()))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("리뷰 좋아요 토글 API 통합 테스트")
	void toggleReviewLike_Success() throws Exception {
		// Given
		ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
			.bookId(testBook.getId())
			.userId(testUser.getId())
			.content("좋아요 버튼 클릭을 테스트할 본문입니다.")
			.rating(5)
			.build();
		ReviewDto createdReview = reviewService.create(createRequest);

		// When & Then
		mockMvc.perform(post("/api/reviews/{reviewId}/like", createdReview.getId())
				.header(HEADER_USER_ID, testUser.getId().toString()))
			.andExpect(status().isOk())
			.andDo(print());
	}
}