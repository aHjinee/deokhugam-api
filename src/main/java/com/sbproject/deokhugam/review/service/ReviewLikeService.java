package com.sbproject.deokhugam.review.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbproject.deokhugam.review.dto.ReviewLikeDto;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.entity.ReviewLike;
import com.sbproject.deokhugam.review.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.review.repository.ReviewLikeRepository;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewLikeService {

	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	@Transactional
	public ReviewLikeDto toggleLike(UUID reviewId, UUID userId) {
		if (reviewId == null) {
			throw new IllegalArgumentException("리뷰 ID는 필수 값입니다.");
		}
		if (userId == null) {
			throw new IllegalArgumentException("요청자 아이디 누락");
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> UserNotFoundException.withId(userId));

		Optional<ReviewLike> reviewLikeOpt = reviewLikeRepository.findByUser_IdAndReview_Id(userId, reviewId);

		if (reviewLikeOpt.isPresent()) {
			reviewLikeRepository.delete(reviewLikeOpt.get());
			review.decreaseLikeCount(); // Review 엔티티 카운트 정합성 차감 (-1)
			return new ReviewLikeDto(reviewId, userId, false);
		} else {
			ReviewLike reviewLike = ReviewLike.builder()
				.review(review)
				.user(user)
				.build();

			reviewLikeRepository.save(reviewLike);
			review.increaseLikeCount(); // Review 엔티티 카운트 정합성 누적 (+1)
			return new ReviewLikeDto(reviewId, userId, true);
		}
	}
}
