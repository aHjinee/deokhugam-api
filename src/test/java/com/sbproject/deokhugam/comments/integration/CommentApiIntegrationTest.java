package com.sbproject.deokhugam.comments.integration;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentApiIntegrationTest {

	private static final String HEADER_USER_ID = "Deokhugam-Request-User-ID";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	private User user;
	private User otherUser;
	private Review review;

	@BeforeEach
	void setUp() {
		user = userRepository.save(User.builder()
			.email("comment-user@example.com")
			.nickname("댓글작성자")
			.password("password")
			.build());

		otherUser = userRepository.save(User.builder()
			.email("comment-other@example.com")
			.nickname("다른사용자")
			.password("password")
			.build());

		Book book = bookRepository.save(Book.builder()
			.isbn("9791190000001")
			.title("댓글 테스트 도서")
			.author("테스트 저자")
			.description("댓글 통합 테스트용 도서입니다.")
			.publisher("테스트 출판사")
			.publishedDate(LocalDate.of(2026, 1, 1))
			.thumbnailUrl("https://example.com/comment-book.png")
			.reviewCount(0)
			.totalScore(0)
			.rating(0.0)
			.build());

		review = reviewRepository.save(Review.builder()
			.user(user)
			.book(book)
			.content("댓글 통합 테스트용 리뷰입니다.")
			.rating(5)
			.likeCount(0)
			.commentCount(0)
			.build());
	}

	@Test
	@DisplayName("댓글 등록 API 성공")
	void createComment_success() throws Exception {
		// given
		Map<String, Object> request = createCommentRequest("새 댓글입니다.");

		// when & then
		mockMvc.perform(post("/api/comments")
				.header(HEADER_USER_ID, user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.reviewId").value(review.getId().toString()))
			.andExpect(jsonPath("$.userId").value(user.getId().toString()))
			.andExpect(jsonPath("$.userNickname").value("댓글작성자"))
			.andExpect(jsonPath("$.content").value("새 댓글입니다."));
	}

	@Test
	@DisplayName("댓글 등록 API 성공 - 작성자는 요청 헤더의 사용자 ID를 사용")
	void createComment_success_useHeaderUserId() throws Exception {
		// given
		Map<String, Object> request = createCommentRequest(review.getId(), otherUser.getId(), "헤더 기준 댓글입니다.");

		// when & then
		mockMvc.perform(post("/api/comments")
				.header(HEADER_USER_ID, user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.reviewId").value(review.getId().toString()))
			.andExpect(jsonPath("$.userId").value(user.getId().toString()))
			.andExpect(jsonPath("$.userNickname").value("댓글작성자"))
			.andExpect(jsonPath("$.content").value("헤더 기준 댓글입니다."));
	}

	@Test
	@DisplayName("댓글 등록 API 실패 - 리뷰가 없음")
	void createComment_fail_reviewNotFound() throws Exception {
		// given
		Map<String, Object> request = createCommentRequest(UUID.randomUUID(), user.getId(), "새 댓글입니다.");

		// when & then
		mockMvc.perform(post("/api/comments")
				.header(HEADER_USER_ID, user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"));
	}

	@Test
	@DisplayName("댓글 등록 API 실패 - 내용이 비어 있음")
	void createComment_fail_blankContent() throws Exception {
		// given
		Map<String, Object> request = createCommentRequest(" ");

		// when & then
		mockMvc.perform(post("/api/comments")
				.header(HEADER_USER_ID, user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	@DisplayName("댓글 목록 조회 API 성공")
	void findComments_success() throws Exception {
		// given
		createComment("목록 조회 댓글");

		// when & then
		mockMvc.perform(get("/api/comments")
				.param("reviewId", review.getId().toString())
				.param("limit", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].content").value("목록 조회 댓글"))
			.andExpect(jsonPath("$.size").value(1))
			.andExpect(jsonPath("$.hasNext").value(false));
	}

	@Test
	@DisplayName("댓글 목록 조회 API 실패 - 리뷰가 없음")
	void findComments_fail_reviewNotFound() throws Exception {
		// given
		UUID notExistingReviewId = UUID.randomUUID();

		// when & then
		mockMvc.perform(get("/api/comments")
				.param("reviewId", notExistingReviewId.toString()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("REVIEW_NOT_FOUND"));
	}

	@Test
	@DisplayName("댓글 목록 조회 API 실패 - 정렬 방향이 잘못됨")
	void findComments_fail_invalidDirection() throws Exception {
		// when & then
		mockMvc.perform(get("/api/comments")
				.param("reviewId", review.getId().toString())
				.param("direction", "WRONG"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("댓글 목록 조회 API 실패 - limit이 1보다 작음")
	void findComments_fail_invalidLimit() throws Exception {
		// when & then
		mockMvc.perform(get("/api/comments")
				.param("reviewId", review.getId().toString())
				.param("limit", "0"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("댓글 목록 조회 API 실패 - cursor가 있는데 after가 없음")
	void findComments_fail_cursorWithoutAfter() throws Exception {
		// when & then
		mockMvc.perform(get("/api/comments")
				.param("reviewId", review.getId().toString())
				.param("cursor", "2026-06-25T01:00:00Z"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("댓글 수정 API 성공")
	void updateComment_success() throws Exception {
		// given
		UUID commentId = createComment("수정 전 댓글");
		Map<String, String> request = Map.of("content", "수정 후 댓글");

		// when & then
		mockMvc.perform(patch("/api/comments/{commentId}", commentId)
				.header(HEADER_USER_ID, user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(commentId.toString()))
			.andExpect(jsonPath("$.content").value("수정 후 댓글"));
	}

	@Test
	@DisplayName("댓글 수정 API 실패 - 작성자가 아님")
	void updateComment_fail_notOwner() throws Exception {
		// given
		UUID commentId = createComment("수정 전 댓글");
		Map<String, String> request = Map.of("content", "수정 후 댓글");

		// when & then
		mockMvc.perform(patch("/api/comments/{commentId}", commentId)
				.header(HEADER_USER_ID, otherUser.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("COMMENT_NOT_OWNED"));
	}

	@Test
	@DisplayName("댓글 논리 삭제 API 성공")
	void deleteComment_success() throws Exception {
		// given
		UUID commentId = createComment("논리 삭제 댓글");

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}", commentId)
				.header(HEADER_USER_ID, user.getId().toString()))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/comments/{commentId}", commentId))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("댓글 논리 삭제 API 실패 - 작성자가 아님")
	void deleteComment_fail_notOwner() throws Exception {
		// given
		UUID commentId = createComment("논리 삭제 댓글");

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}", commentId)
				.header(HEADER_USER_ID, otherUser.getId().toString()))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("COMMENT_NOT_OWNED"));
	}

	@Test
	@DisplayName("댓글 논리 삭제 API 실패 - 댓글이 없음")
	void deleteComment_fail_notFound() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}", commentId)
				.header(HEADER_USER_ID, user.getId().toString()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("COMMENT_NOT_FOUND"));
	}

	@Test
	@DisplayName("댓글 물리 삭제 API 성공")
	void hardDeleteComment_success() throws Exception {
		// given
		UUID commentId = createComment("물리 삭제 댓글");

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
				.header(HEADER_USER_ID, user.getId().toString()))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/comments/{commentId}", commentId))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("댓글 물리 삭제 API 실패 - 작성자가 아님")
	void hardDeleteComment_fail_notOwner() throws Exception {
		// given
		UUID commentId = createComment("물리 삭제 댓글");

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
				.header(HEADER_USER_ID, otherUser.getId().toString()))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("COMMENT_NOT_OWNED"));
	}

	@Test
	@DisplayName("댓글 물리 삭제 API 실패 - 댓글이 없음")
	void hardDeleteComment_fail_notFound() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
				.header(HEADER_USER_ID, user.getId().toString()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("COMMENT_NOT_FOUND"));
	}

	@Test
	@DisplayName("댓글 조회 API 실패 - 존재하지 않는 댓글")
	void findComment_fail_notFound() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();

		// when & then
		mockMvc.perform(get("/api/comments/{commentId}", commentId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("COMMENT_NOT_FOUND"));
	}

	private UUID createComment(String content) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/comments")
				.header(HEADER_USER_ID, user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createCommentRequest(content))))
			.andExpect(status().isCreated())
			.andReturn();

		Map<String, Object> response = objectMapper.readValue(
			result.getResponse().getContentAsString(),
			new TypeReference<>() {
			}
		);
		return UUID.fromString(response.get("id").toString());
	}

	private Map<String, Object> createCommentRequest(String content) {
		return createCommentRequest(review.getId(), user.getId(), content);
	}

	private Map<String, Object> createCommentRequest(UUID reviewId, UUID userId, String content) {
		return Map.of(
			"reviewId", reviewId.toString(),
			"userId", userId.toString(),
			"content", content
		);
	}
}
