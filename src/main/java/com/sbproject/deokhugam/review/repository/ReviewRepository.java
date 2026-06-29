package com.sbproject.deokhugam.review.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.repository.querydsl.ReviewQueryRepository;

import jakarta.annotation.Nullable;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewQueryRepository {

	@Override
	@EntityGraph(attributePaths = {"user", "book"})
	Optional<Review> findById(@Nullable UUID id);
}
