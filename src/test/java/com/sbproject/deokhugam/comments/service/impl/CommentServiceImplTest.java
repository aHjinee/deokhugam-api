package com.sbproject.deokhugam.comments.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.entity.Comment;
import com.sbproject.deokhugam.comments.exception.CommentNotFoundException;
import com.sbproject.deokhugam.comments.repository.CommentRepository;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.user.entity.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentServiceImpl commentService;

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
}
