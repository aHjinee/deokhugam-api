package com.sbproject.deokhugam.review.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbproject.deokhugam.review.dto.ReviewLikeDto;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.entity.ReviewLike;
import com.sbproject.deokhugam.review.repository.ReviewLikeRepository;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	@Transactional
	public boolean toggleLike(UUID postId, UUID userId) {
		boolean exists = reviewLikeRepository.existsByUser_IdAndReview_Id(userId, postId);
		if (exists) {
			reviewLikeRepository.deleteByUser_IdAndReview_Id(userId, postId);
			return false; // 좋아요 해제
		} else {
			Review review = reviewRepository.getReferenceById(postId);   // 프록시 참조 - 불필요한 SELECT 방지
			User user = userRepository.getReferenceById(userId);   // 프록시 참조 - 불필요한 SELECT 방지
			reviewLikeRepository.save(ReviewLike.builder().review(review).user(user).build());
			return true; // 좋아요 추가
		}
	}
}
