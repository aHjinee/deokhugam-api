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

import com.sbproject.deokhugam.comments.dto.CommentCreateRequest;
import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.entity.Comment;
import com.sbproject.deokhugam.comments.exception.CommentNotFoundException;
import com.sbproject.deokhugam.comments.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.comments.repository.CommentRepository;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Pageable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CommentServiceImpl commentService;

	@Test
	void createCommentSavesCommentAndReturnsCommentDto() {
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID requestBodyUserId = UUID.randomUUID();
		User user = User.builder()
			.id(userId)
			.nickname("woody")
			.email("woody@deokhugam.com")
			.password("password")
			.build();
		Review review = Review.builder()
			.id(reviewId)
			.user(user)
			.content("review content")
			.rating(5)
			.commentCount(0)
			.build();
		CommentCreateRequest request = new CommentCreateRequest(reviewId, requestBodyUserId, "new comment");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(commentRepository.save(any(Comment.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		CommentDto result = commentService.createComment(request, userId);

		assertThat(result.getReviewId()).isEqualTo(reviewId);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getUserNickname()).isEqualTo("woody");
		assertThat(result.getContent()).isEqualTo("new comment");
		assertThat(review.getCommentCount()).isEqualTo(1);

		ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
		verify(commentRepository).save(commentCaptor.capture());
		assertThat(commentCaptor.getValue().getReview()).isEqualTo(review);
		assertThat(commentCaptor.getValue().getUser()).isEqualTo(user);
		assertThat(commentCaptor.getValue().getContent()).isEqualTo("new comment");
	}

	@Test
	void createCommentThrowsWhenReviewDoesNotExist() {
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "new comment");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> commentService.createComment(request, userId))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	void createCommentThrowsWhenUserDoesNotExist() {
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Review review = Review.builder()
			.id(reviewId)
			.content("review content")
			.rating(5)
			.build();
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "new comment");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> commentService.createComment(request, userId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	void findCommentReturnsCommentDto() {
		UUID commentId = UUID.randomUUID();
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		User user = User.builder()
			.id(userId)
			.nickname("우디")
			.email("woody@deokhugam.com")
			.password("password")
			.build();
		Review review = Review.builder()
			.id(reviewId)
			.user(user)
			.content("리뷰 내용")
			.rating(5)
			.build();
		Comment comment = Comment.builder()
			.id(commentId)
			.review(review)
			.user(user)
			.content("좋은 리뷰입니다.")
			.build();

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId))
			.thenReturn(Optional.of(comment));

		CommentDto result = commentService.findComment(commentId);

		assertThat(result.getId()).isEqualTo(commentId);
		assertThat(result.getReviewId()).isEqualTo(reviewId);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getUserNickname()).isEqualTo("우디");
		assertThat(result.getContent()).isEqualTo("좋은 리뷰입니다.");
	}

	@Test
	void findCommentThrowsWhenCommentDoesNotExist() {
		UUID commentId = UUID.randomUUID();

		when(commentRepository.findByIdAndDeletedAtIsNull(commentId))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> commentService.findComment(commentId))
			.isInstanceOf(CommentNotFoundException.class);
	}

	@Test
	void findCommentsReturnsCursorPage() {
		UUID reviewId = UUID.randomUUID();
		User user = User.builder()
			.id(UUID.randomUUID())
			.nickname("woody")
			.email("woody@deokhugam.com")
			.password("password")
			.build();
		Review review = Review.builder()
			.id(reviewId)
			.user(user)
			.content("review content")
			.rating(5)
			.build();
		Comment firstComment = Comment.builder()
			.id(UUID.randomUUID())
			.review(review)
			.user(user)
			.content("first comment")
			.createdAt(Instant.parse("2026-06-25T01:00:00Z"))
			.updatedAt(Instant.parse("2026-06-25T01:00:00Z"))
			.build();
		Comment secondComment = Comment.builder()
			.id(UUID.randomUUID())
			.review(review)
			.user(user)
			.content("second comment")
			.createdAt(Instant.parse("2026-06-25T00:00:00Z"))
			.updatedAt(Instant.parse("2026-06-25T00:00:00Z"))
			.build();

		when(reviewRepository.existsById(reviewId)).thenReturn(true);
		when(commentRepository.findByReview_IdAndDeletedAtIsNull(
			eq(reviewId),
			any(Pageable.class)
		)).thenReturn(List.of(firstComment, secondComment));
		when(commentRepository.countByReview_IdAndDeletedAtIsNull(reviewId)).thenReturn(2L);

		SlicePageResponse<CommentDto> result = commentService.findComments(
			reviewId,
			"DESC",
			null,
			null,
			1
		);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("first comment");
		assertThat(result.getNextCursor()).isEqualTo("2026-06-25T01:00:00Z");
		assertThat(result.getNextAfter()).isEqualTo(Instant.parse("2026-06-25T01:00:00Z"));
		assertThat(result.getSize()).isEqualTo(1);
		assertThat(result.getTotalElements()).isEqualTo(2L);
		assertThat(result.isHasNext()).isTrue();
	}

	@Test
	void findCommentsThrowsWhenReviewDoesNotExist() {
		UUID reviewId = UUID.randomUUID();

		when(reviewRepository.existsById(reviewId)).thenReturn(false);

		assertThatThrownBy(() -> commentService.findComments(reviewId, "DESC", null, null, 50))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	void findCommentsThrowsWhenCursorExistsWithoutAfter() {
		UUID reviewId = UUID.randomUUID();

		when(reviewRepository.existsById(reviewId)).thenReturn(true);

		assertThatThrownBy(() -> commentService.findComments(
			reviewId,
			"DESC",
			"2026-06-25T01:00:00Z",
			null,
			50
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("after");
	}

	@Test
	void findCommentsThrowsWhenCursorIsInvalid() {
		UUID reviewId = UUID.randomUUID();

		when(reviewRepository.existsById(reviewId)).thenReturn(true);

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
	void findCommentsUsesAfterWhenOnlyAfterExists() {
		UUID reviewId = UUID.randomUUID();
		Instant after = Instant.parse("2026-06-25T01:00:00Z");
		User user = User.builder()
			.id(UUID.randomUUID())
			.nickname("woody")
			.email("woody@deokhugam.com")
			.password("password")
			.build();
		Review review = Review.builder()
			.id(reviewId)
			.user(user)
			.content("review content")
			.rating(5)
			.build();
		Comment comment = Comment.builder()
			.id(UUID.randomUUID())
			.review(review)
			.user(user)
			.content("comment after cursor")
			.createdAt(Instant.parse("2026-06-25T00:00:00Z"))
			.updatedAt(Instant.parse("2026-06-25T00:00:00Z"))
			.build();

		when(reviewRepository.existsById(reviewId)).thenReturn(true);
		when(commentRepository.findByReview_IdAndDeletedAtIsNullAndCreatedAtLessThan(
			eq(reviewId),
			eq(after),
			any(Pageable.class)
		)).thenReturn(List.of(comment));
		when(commentRepository.countByReview_IdAndDeletedAtIsNull(reviewId)).thenReturn(1L);

		SlicePageResponse<CommentDto> result = commentService.findComments(
			reviewId,
			"DESC",
			null,
			after,
			50
		);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("comment after cursor");
		assertThat(result.isHasNext()).isFalse();
	}
}
