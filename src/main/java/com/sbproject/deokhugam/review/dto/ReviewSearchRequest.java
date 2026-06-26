package com.sbproject.deokhugam.review.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSearchRequest {
	private UUID userId;        // 작성자 ID 필요 없을지도
	private UUID bookId;        // 도서 ID 이하동문 swagger는 있는데 사용처 모름

	private String keyword;     // 검색 키워드 (작성자 닉네임 또는 내용)

	private String orderBy;     // 정렬 기준: createdAt / rating (기본값: createdAt)
	private String direction;   // 정렬 방향: ASC / DESC (기본값: DESC)
	private String cursor;      // 커서 페이지네이션용 커서 ID
	private Instant after;      // 보조 커서 (createdAt 기준 시각 필터)
	private Integer limit;      // 페이지 크기 (기본값: 50) [cite: 5, 6]

	private UUID requestUserId;          // 쿼리 스트링 파라미터 요청자 ID [cite: 7]
	private UUID deokhugamRequestUserId; // 헤더에서 추출한 요청자 ID (Deokhugam-Request-User-ID)
}
