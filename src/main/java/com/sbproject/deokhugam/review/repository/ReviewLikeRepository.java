package com.sbproject.deokhugam.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbproject.deokhugam.review.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

}