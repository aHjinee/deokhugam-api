package com.sbproject.deokhugam.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.repository.querydsl.ReviewQueryRepository;

public interface reviewRepository extends JpaRepository<Review, UUID>, ReviewQueryRepository {
}
