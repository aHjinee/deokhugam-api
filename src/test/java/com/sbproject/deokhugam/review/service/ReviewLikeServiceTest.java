package com.sbproject.deokhugam.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.sbproject.deokhugam.review.dto.ReviewLikeDto;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.entity.ReviewLike;
import com.sbproject.deokhugam.review.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.review.repository.ReviewLikeRepository;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceTest {

	@Mock
	private ReviewLikeRepository reviewLikeRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ReviewLikeService reviewLikeService;

	@Test
	@DisplayName("좋아요 토글 성공: 기존에 좋아요가 없다면 새로 생성하고 likeCount를 1 증가시킨다")
	void toggleLikeCreatesLikeWhenNotExists() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		User user = User.builder().id(userId).build();
		Review review = Review.builder().id(reviewId).likeCount(0).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(reviewLikeRepository.findByUser_IdAndReview_Id(userId, reviewId)).thenReturn(Optional.empty());
		when(reviewLikeRepository.save(any(ReviewLike.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// when
		ReviewLikeDto result = reviewLikeService.toggleLike(reviewId, userId);

		// then
		assertThat(result.isLiked()).isTrue();
		assertThat(review.getLikeCount()).isEqualTo(1);

		ArgumentCaptor<ReviewLike> likeCaptor = ArgumentCaptor.forClass(ReviewLike.class);
		verify(reviewLikeRepository).save(likeCaptor.capture());
		assertThat(likeCaptor.getValue().getReview()).isEqualTo(review);
		assertThat(likeCaptor.getValue().getUser()).isEqualTo(user);
	}

	@Test
	@DisplayName("좋아요 토글 성공: 기존에 좋아요가 이미 존재한다면 삭제하고 likeCount를 1 감소시킨다")
	void toggleLikeRemovesLikeWhenAlreadyExists() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		User user = User.builder().id(userId).build();
		Review review = Review.builder().id(reviewId).likeCount(1).build();
		ReviewLike reviewLike = ReviewLike.builder().id(UUID.randomUUID()).review(review).user(user).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(reviewLikeRepository.findByUser_IdAndReview_Id(userId, reviewId)).thenReturn(Optional.of(reviewLike));

		// when
		ReviewLikeDto result = reviewLikeService.toggleLike(reviewId, userId);

		// then
		assertThat(result.isLiked()).isFalse();
		assertThat(review.getLikeCount()).isEqualTo(0);
		verify(reviewLikeRepository).delete(reviewLike);
	}

	@Test
	@DisplayName("좋아요 토글 실패: 존재하지 않는 리뷰 ID로 토글 요청 시 ReviewNotFoundException이 발생한다")
	void toggleLikeThrowsWhenReviewDoesNotExist() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewLikeService.toggleLike(reviewId, userId))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	@DisplayName("좋아요 토글 실패: 존재하지 않는 유저 ID로 토글 요청 시 UserNotFoundException이 발생한다")
	void toggleLikeThrowsWhenUserDoesNotExist() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Review review = Review.builder().id(reviewId).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewLikeService.toggleLike(reviewId, userId))
			.isInstanceOf(UserNotFoundException.class);
	}
}