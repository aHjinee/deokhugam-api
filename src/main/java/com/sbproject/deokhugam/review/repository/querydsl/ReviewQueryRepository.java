package com.sbproject.deokhugam.review.repository.querydsl;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;

public interface ReviewQueryRepository {
	SlicePageResponse<ReviewDto> searchCursorSorted(ReviewSearchRequest request);
}
