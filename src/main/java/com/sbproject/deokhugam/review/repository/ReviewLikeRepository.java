package com.sbproject.deokhugam.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbproject.deokhugam.review.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {
	boolean existsByUser_IdAndReview_Id(UUID userId, UUID reviewId);
	void deleteByUser_IdAndReview_Id(UUID userId, UUID reviewId);
}