package com.sbproject.deokhugam.review.service.Impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewCreateRequest;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;
import com.sbproject.deokhugam.review.dto.ReviewUpdateRequest;
import com.sbproject.deokhugam.review.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {
	@Override
	public ReviewDto create(ReviewCreateRequest request) {
		return null;
	}

	@Override
	public ReviewDto update(UUID reviewID, UUID userId, ReviewUpdateRequest request) {
		return null;
	}

	@Override
	public ReviewDto findById(UUID reviewID, UUID userId) {
		return null;
	}

	@Override
	public void delete(UUID reviewID, UUID userId) {

	}

	@Override
	public void deleteSoft(UUID reviewID, UUID userId) {

	}

	@Override
	public SlicePageResponse<ReviewDto> findAll(UUID userID, ReviewSearchRequest request) {
		return null;
	}
}
