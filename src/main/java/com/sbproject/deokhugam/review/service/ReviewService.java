package com.sbproject.deokhugam.review.service;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewCreateRequest;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;
import com.sbproject.deokhugam.review.dto.ReviewUpdateRequest;

public interface ReviewService {
	public ReviewDto create(ReviewCreateRequest request);

	public ReviewDto update(UUID reviewID, UUID userId, ReviewUpdateRequest request);

	public ReviewDto findById(UUID reviewID, UUID userId);

	public void delete(UUID reviewID, UUID userId);

	public void deleteSoft(UUID reviewID, UUID userId);

	public SlicePageResponse<ReviewDto> findAll(UUID userID, ReviewSearchRequest request);
}
