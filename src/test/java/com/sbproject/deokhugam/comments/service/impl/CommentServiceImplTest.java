package com.sbproject.deokhugam.comments.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sbproject.deokhugam.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.sbproject.deokhugam.comments.dto.CommentCreateRequest;
import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.dto.CommentUpdateRequest;
import com.sbproject.deokhugam.comments.entity.Comment;
import com.sbproject.deokhugam.comments.exception.CommentNotFoundException;
import com.sbproject.deokhugam.comments.exception.CommentNotOwnedException;
import com.sbproject.deokhugam.comments.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.comments.repository.CommentRepository;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private CommentServiceImpl commentService;

	@Test
	@DisplayName("댓글 등록 성공")
	void createComment_success() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = createUser(userId);
		Review review = createReview(reviewId, user, 0);
		CommentCreateRequest request = new CommentCreateRequest(reviewId, UUID.randomUUID(), "새 댓글");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		CommentDto result = commentService.createComment(request, userId);

		// then
		assertThat(result.getReviewId()).isEqualTo(reviewId);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getContent()).isEqualTo("새 댓글");
		assertThat(review.getCommentCount()).isEqualTo(1);

		ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
		verify(commentRepository).save(commentCaptor.capture());
		assertThat(commentCaptor.getValue().getReview()).isEqualTo(review);
		assertThat(commentCaptor.getValue().getUser()).isEqualTo(user);
	}

	@Test
	@DisplayName("댓글 등록 실패 - 리뷰가 없음")
	void createComment_fail_reviewNotFound() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "새 댓글");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.createComment(request, userId))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 등록 실패 - 사용자가 없음")
	void createComment_fail_userNotFound() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Review review = createReview(reviewId, createUser(UUID.randomUUID()), 0);
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "새 댓글");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.createComment(request, userId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 상세 조회 성공")
	void findComment_success() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = createUser(userId);
		Review review = createReview(reviewId, user, 0);
		Comment comment = createComment(commentId, review, user, "조회할 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when
		CommentDto result = commentService.findComment(commentId);

		// then
		assertThat(result.getId()).isEqualTo(commentId);
		assertThat(result.getReviewId()).isEqualTo(reviewId);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getContent()).isEqualTo("조회할 댓글");
	}

	@Test
	@DisplayName("댓글 상세 조회 실패 - 댓글이 없음")
	void findComment_fail_notFound() {
		// given
		UUID commentId = UUID.randomUUID();
		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.findComment(commentId))
			.isInstanceOf(CommentNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 수정 성공")
	void updateComment_success() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = createUser(userId);
		Review review = createReview(UUID.randomUUID(), user, 0);
		Comment comment = createComment(commentId, review, user, "수정 전 댓글");
		CommentUpdateRequest request = new CommentUpdateRequest("수정 후 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when
		CommentDto result = commentService.updateComment(commentId, request, userId);

		// then
		assertThat(result.getContent()).isEqualTo("수정 후 댓글");
		assertThat(comment.getContent()).isEqualTo("수정 후 댓글");
	}

	@Test
	@DisplayName("댓글 수정 실패 - 댓글이 없음")
	void updateComment_fail_notFound() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		CommentUpdateRequest request = new CommentUpdateRequest("수정 후 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.updateComment(commentId, request, userId))
			.isInstanceOf(CommentNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 수정 실패 - 작성자가 아님")
	void updateComment_fail_notOwner() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		UUID requesterId = UUID.randomUUID();
		User owner = createUser(ownerId);
		Comment comment = createComment(commentId, createReview(UUID.randomUUID(), owner, 0), owner, "수정 전 댓글");
		CommentUpdateRequest request = new CommentUpdateRequest("수정 후 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when & then
		assertThatThrownBy(() -> commentService.updateComment(commentId, request, requesterId))
			.isInstanceOf(CommentNotOwnedException.class);
		assertThat(comment.getContent()).isEqualTo("수정 전 댓글");
	}

	@Test
	@DisplayName("댓글 논리 삭제 성공")
	void deleteComment_success() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = createUser(userId);
		Review review = createReview(UUID.randomUUID(), user, 1);
		Comment comment = createComment(commentId, review, user, "삭제할 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when
		commentService.deleteComment(commentId, userId);

		// then
		assertThat(comment.getDeletedAt()).isNotNull();
		assertThat(review.getCommentCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("댓글 논리 삭제 실패 - 댓글이 없음")
	void deleteComment_fail_notFound() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.deleteComment(commentId, userId))
			.isInstanceOf(CommentNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 논리 삭제 실패 - 작성자가 아님")
	void deleteComment_fail_notOwner() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		UUID requesterId = UUID.randomUUID();
		User owner = createUser(ownerId);
		Review review = createReview(UUID.randomUUID(), owner, 1);
		Comment comment = createComment(commentId, review, owner, "삭제할 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when & then
		assertThatThrownBy(() -> commentService.deleteComment(commentId, requesterId))
			.isInstanceOf(CommentNotOwnedException.class);
		assertThat(comment.getDeletedAt()).isNull();
		assertThat(review.getCommentCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("댓글 물리 삭제 성공")
	void hardDeleteComment_success() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = createUser(userId);
		Review review = createReview(UUID.randomUUID(), user, 1);
		Comment comment = createComment(commentId, review, user, "삭제할 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when
		commentService.hardDeleteComment(commentId, userId);

		// then
		verify(commentRepository).delete(comment);
		assertThat(review.getCommentCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("댓글 물리 삭제 실패 - 댓글이 없음")
	void hardDeleteComment_fail_notFound() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> commentService.hardDeleteComment(commentId, userId))
			.isInstanceOf(CommentNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 물리 삭제 실패 - 작성자가 아님")
	void hardDeleteComment_fail_notOwner() {
		// given
		UUID commentId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		UUID requesterId = UUID.randomUUID();
		User owner = createUser(ownerId);
		Review review = createReview(UUID.randomUUID(), owner, 1);
		Comment comment = createComment(commentId, review, owner, "삭제할 댓글");

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(comment));

		// when & then
		assertThatThrownBy(() -> commentService.hardDeleteComment(commentId, requesterId))
			.isInstanceOf(CommentNotOwnedException.class);
		assertThat(review.getCommentCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("댓글 목록 조회 성공")
	void findComments_success() {
		// given
		UUID reviewId = UUID.randomUUID();
		User user = createUser(UUID.randomUUID());
		Review review = createReview(reviewId, user, 0);
		Comment firstComment = createCommentWithTime(
			UUID.randomUUID(),
			review,
			user,
			"첫 번째 댓글",
			Instant.parse("2026-06-25T01:00:00Z")
		);
		Comment secondComment = createCommentWithTime(
			UUID.randomUUID(),
			review,
			user,
			"두 번째 댓글",
			Instant.parse("2026-06-25T00:00:00Z")
		);

		when(reviewRepository.existsById(reviewId)).thenReturn(true);
		when(commentRepository.findByReview_IdAndDeletedAtIsNull(eq(reviewId), any(Pageable.class)))
			.thenReturn(List.of(firstComment, secondComment));
		when(commentRepository.countByReview_IdAndDeletedAtIsNull(reviewId)).thenReturn(2L);

		// when
		SlicePageResponse<CommentDto> result = commentService.findComments(reviewId, "DESC", null, null, 1);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("첫 번째 댓글");
		assertThat(result.getNextCursor()).isEqualTo("2026-06-25T01:00:00Z");
		assertThat(result.getNextAfter()).isEqualTo(Instant.parse("2026-06-25T01:00:00Z"));
		assertThat(result.isHasNext()).isTrue();
	}

	@Test
	@DisplayName("댓글 목록 조회 실패 - 리뷰가 없음")
	void findComments_fail_reviewNotFound() {
		// given
		UUID reviewId = UUID.randomUUID();
		when(reviewRepository.existsById(reviewId)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> commentService.findComments(reviewId, "DESC", null, null, 50))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	@DisplayName("댓글 목록 조회 실패 - cursor가 있는데 after가 없음")
	void findComments_fail_cursorWithoutAfter() {
		// given
		UUID reviewId = UUID.randomUUID();
		when(reviewRepository.existsById(reviewId)).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> commentService.findComments(reviewId, "DESC", "2026-06-25T01:00:00Z", null, 50))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("after");
	}

	@Test
	@DisplayName("댓글 목록 조회 실패 - cursor 형식이 잘못됨")
	void findComments_fail_invalidCursor() {
		// given
		UUID reviewId = UUID.randomUUID();
		when(reviewRepository.existsById(reviewId)).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> commentService.findComments(
			reviewId,
			"DESC",
			"abc",
			Instant.parse("2026-06-25T01:00:00Z"),
			50
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("cursor");
	}

	@Test
	@DisplayName("댓글 목록 조회 - after만 있으면 보조 커서로 조회")
	void findComments_afterOnly() {
		// given
		UUID reviewId = UUID.randomUUID();
		Instant after = Instant.parse("2026-06-25T01:00:00Z");
		User user = createUser(UUID.randomUUID());
		Review review = createReview(reviewId, user, 0);
		Comment comment = createCommentWithTime(
			UUID.randomUUID(),
			review,
			user,
			"커서 이후 댓글",
			Instant.parse("2026-06-25T00:00:00Z")
		);

		when(reviewRepository.existsById(reviewId)).thenReturn(true);
		when(commentRepository.findByReview_IdAndDeletedAtIsNullAndCreatedAtLessThan(
			eq(reviewId),
			eq(after),
			any(Pageable.class)
		)).thenReturn(List.of(comment));
		when(commentRepository.countByReview_IdAndDeletedAtIsNull(reviewId)).thenReturn(1L);

		// when
		SlicePageResponse<CommentDto> result = commentService.findComments(reviewId, "DESC", null, after, 50);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("커서 이후 댓글");
		assertThat(result.isHasNext()).isFalse();
	}

	private User createUser(UUID userId) {
		return User.builder()
			.id(userId)
			.email("comment@test.com")
			.nickname("댓글러")
			.password("password")
			.build();
	}

	private Review createReview(UUID reviewId, User user, int commentCount) {
		return Review.builder()
			.id(reviewId)
			.user(user)
			.content("리뷰 내용")
			.rating(5)
			.commentCount(commentCount)
			.build();
	}

	private Comment createComment(UUID commentId, Review review, User user, String content) {
		return Comment.builder()
			.id(commentId)
			.review(review)
			.user(user)
			.content(content)
			.build();
	}

	private Comment createCommentWithTime(UUID commentId, Review review, User user, String content, Instant createdAt) {
		return Comment.builder()
			.id(commentId)
			.review(review)
			.user(user)
			.content(content)
			.createdAt(createdAt)
			.updatedAt(createdAt)
			.build();
	}
}
