package com.sbproject.deokhugam.review.service;

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
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> UserNotFoundException.withId(userId));

		Optional<ReviewLike> reviewLikeOpt = reviewLikeRepository.findByUser_IdAndReview_Id(userId, reviewId);

		if (reviewLikeOpt.isPresent()) {
			reviewLikeRepository.delete(reviewLikeOpt.get());
			review.decreaseLikeCount();
			return new ReviewLikeDto(reviewId, userId, false);
		} else {
			ReviewLike reviewLike = ReviewLike.builder()
				.review(review)
				.user(user)
				.build();

			reviewLikeRepository.save(reviewLike);
			review.increaseLikeCount();
			return new ReviewLikeDto(reviewId, userId, true);
		}
	}
}
