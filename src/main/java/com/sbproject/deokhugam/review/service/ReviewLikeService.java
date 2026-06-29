package com.sbproject.deokhugam.review.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbproject.deokhugam.review.dto.ReviewLikeDto;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.entity.ReviewLike;
import com.sbproject.deokhugam.review.repository.ReviewLikeRepository;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
			throw new IllegalArgumentException("유저 ID(userId)는 필수 값입니다.");
		}

		// 1. 엔티티 존재 여부 확인 및 조회
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다. id: " + reviewId));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> UserNotFoundException.withId(userId));

		// 2. 유무 판별과 객체 확보를 동시에 처리
		Optional<ReviewLike> reviewLikeOpt = reviewLikeRepository.findByUser_IdAndReview_Id(userId, reviewId);

		if (reviewLikeOpt.isPresent()) {
			// 데이터가 존재하면 영속성 컨텍스트에 보관된 객체를 안전하게 단건 물리 삭제
			reviewLikeRepository.delete(reviewLikeOpt.get());
			review.decreaseLikeCount(); // Review 엔티티 카운트 정합성 차감 (-1)
			return new ReviewLikeDto(reviewId, userId, false);
		} else {
			// 데이터가 존재하지 않으면 새로 생성하여 영속화
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
