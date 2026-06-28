package com.sbproject.deokhugam.review.repository.querydsl;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbproject.deokhugam.book.entity.QBook;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;
import com.sbproject.deokhugam.review.entity.QReview;
import com.sbproject.deokhugam.review.entity.QReviewLike;
import com.sbproject.deokhugam.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

	private final JPAQueryFactory queryFactory;

	private static final QReview r = QReview.review;
	private static final QUser u = QUser.user;
	private static final QBook b = QBook.book;
	private static final QReviewLike rl = QReviewLike.reviewLike;

	@Override
	public SlicePageResponse<ReviewDto> searchReviewsCursorSorted(ReviewSearchRequest req) {
		BooleanBuilder where = new BooleanBuilder();

		// limit 조건 기본값 50 설정
		int size = (req.getLimit() != null && req.getLimit() > 0) ? req.getLimit() : 50;

		// 1. Soft Delete 필터링
		where.and(r.deletedAt.isNull());

		// 2. Keyword 동적 검색 (도서명 OR 리뷰어 닉네임 OR 리뷰 내용)
		if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
			where.and(
				b.title.containsIgnoreCase(req.getKeyword())
					.or(u.nickname.containsIgnoreCase(req.getKeyword()))
					.or(r.content.containsIgnoreCase(req.getKeyword()))
			);
		}

		// 3. 특정 날짜 이후 필터 (after)
		if (req.getAfter() != null) {
			where.and(r.createdAt.gt(req.getAfter()));
		}

		// 4. 정렬 방향 및 커서(String 포맷의 Instant 형식) 처리
		Order orderDirection = ("ASC".equalsIgnoreCase(req.getDirection())) ? Order.ASC : Order.DESC;
		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

		if ("rating".equalsIgnoreCase(req.getOrderBy())) {
			// 평점순 정렬
			orderSpecifiers.add(new OrderSpecifier<>(orderDirection, r.rating));
			orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, r.createdAt)); // 보조 정렬

			if (req.getCursor() != null && !req.getCursor().isBlank()) {
				try {
					int cursorRating = Integer.parseInt(req.getCursor());
					if (orderDirection == Order.DESC) {
						where.and(r.rating.loe(cursorRating));
					} else {
						where.and(r.rating.goe(cursorRating));
					}
				} catch (NumberFormatException ignored) {}
			}
		} else {
			// 시간순 정렬 (기본값)
			orderSpecifiers.add(new OrderSpecifier<>(orderDirection, r.createdAt));
			orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, r.id)); // 보조 정렬

			if (req.getCursor() != null && !req.getCursor().isBlank()) {
				try {
					Instant cursorInstant = Instant.parse(req.getCursor());
					if (orderDirection == Order.DESC) {
						where.and(r.createdAt.lt(cursorInstant));
					} else {
						where.and(r.createdAt.gt(cursorInstant));
					}
				} catch (DateTimeParseException ignored) {}
			}
		}

		// 5. 평탄화 데이터 쿼리 실행 (ReviewDto 필드 순서와 100% 명확하게 정합성 매핑)
		List<ReviewDto> rowsPlusOne = queryFactory
			.select(Projections.constructor(
				ReviewDto.class,
				r.id,
				b.id,
				b.title,
				b.thumbnailUrl,
				u.id,
				u.nickname,
				r.content,
				r.rating,
				r.likeCount,
				r.commentCount,
				com.querydsl.core.types.dsl.Expressions.asBoolean(false), // likedByMe는 생성자 파라미터 개수를 맞추기 위해 초기화값 바인딩
				r.createdAt,
				r.updatedAt
			))
			.from(r)
			.join(r.book, b)
			.join(r.user, u)
			.where(where)
			.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
			.limit(size + 1L)
			.fetch();

		// 6. Next 페이지 여부 확인 및 slice 절삭
		boolean hasNext = rowsPlusOne.size() > size;
		List<ReviewDto> contents = hasNext ? rowsPlusOne.subList(0, size) : rowsPlusOne;

		// 7. 현재 로그인 유저 식별 (헤더 식별값을 최우선순위로 처리)
		UUID activeUserId = null;
		if (req.getDeokhugamRequestUserId() != null) {
			activeUserId = req.getDeokhugamRequestUserId();
		} else if (req.getRequestUserId() != null) {
			activeUserId = req.getRequestUserId();
		}

		// In-Query 처리를 이용한 likedByMe 일괄 계산 최적화
		List<UUID> likedReviewIds = Collections.emptyList();
		if (activeUserId != null && !contents.isEmpty()) {
			List<UUID> reviewIdsInPage = contents.stream().map(ReviewDto::getId).toList();
			likedReviewIds = queryFactory
				.select(rl.review.id)
				.from(rl)
				.where(rl.user.id.eq(activeUserId).and(rl.review.id.in(reviewIdsInPage)))
				.fetch();
		}

		// 8. 최종 Response 스펙 DTO 리스트 생성 (likedByMe 값 완벽 세팅)
		List<UUID> finalLikedReviewIds = likedReviewIds;
		List<ReviewDto> responseContents = contents.stream().map(flat -> {
			boolean likedByMe = finalLikedReviewIds.contains(flat.getId());
			flat.setLikedByMe(likedByMe);
			return flat;
		}).toList();

		// 9. [🔥 수정 핵심] nextCursor 및 nextAfter 바인딩 방어 로직 추가
		String nextCursorStr = null;
		Instant nextAfterInst = null;

		// 다음 페이지가 존재할 때만 커서 값을 세팅하고, 없으면 null을 유지하여 프론트를 멈춥니다.
		if (hasNext && !responseContents.isEmpty()) {
			ReviewDto lastElement = responseContents.get(responseContents.size() - 1);

			if ("rating".equalsIgnoreCase(req.getOrderBy())) {
				nextCursorStr = String.valueOf(lastElement.getRating());
			} else {
				nextCursorStr = lastElement.getCreatedAt().toString();
			}
			nextAfterInst = lastElement.getCreatedAt();
		}

		long totalElements = responseContents.size();

		return SlicePageResponse.<ReviewDto>builder()
			.content(responseContents)
			.nextCursor(nextCursorStr)
			.nextAfter(nextAfterInst)
			.size(size)
			.totalElements(totalElements)
			.hasNext(hasNext)
			.build();
	}
}