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

		// limit 조건 기본값 50
		int size = (req.getLimit() != null && req.getLimit() > 0) ? req.getLimit() : 50;

		// Soft Delete 필터링
		where.and(r.deletedAt.isNull());

		if (req.getUserId() != null) {
			where.and(r.user.id.eq(req.getUserId()));
		}

		if (req.getBookId() != null) {
			where.and(r.book.id.eq(req.getBookId()));
		}

		// Keyword 동적 검색
		if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
			where.and(
				b.title.containsIgnoreCase(req.getKeyword())
					.or(u.nickname.containsIgnoreCase(req.getKeyword()))
					.or(r.content.containsIgnoreCase(req.getKeyword()))
			);
		}

		// 특정 날짜 이후 필터
		if (req.getAfter() != null) {
			where.and(r.createdAt.gt(req.getAfter()));
		}

		// 정렬 방향 및 커서(String 포맷의 Instant 형식) 처리
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

		// 현재 로그인 유저 식별 (헤더 식별값을 최우선순위로)
		UUID activeUserId = null;
		if (req.getDeokhugamRequestUserId() != null) {
			activeUserId = req.getDeokhugamRequestUserId();
		} else if (req.getRequestUserId() != null) {
			activeUserId = req.getRequestUserId();
		}

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
				rl.user.id.isNotNull(),
				r.createdAt,
				r.updatedAt
			))
			.from(r)
			.join(r.book, b)
			.join(r.user, u)
			.leftJoin(rl).on(rl.review.id.eq(r.id).and(activeUserId != null ? rl.user.id.eq(activeUserId) : rl.user.id.isNull()))
			.where(where)
			.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
			.limit(size + 1L)
			.fetch();

		// Next 페이지 여부 확인 및 slice 절삭
		boolean hasNext = rowsPlusOne.size() > size;
		List<ReviewDto> responseContents = hasNext ? rowsPlusOne.subList(0, size) : rowsPlusOne;

		// nextCursor 및 nextAfter 바인딩 방어
		String nextCursorStr = null;
		Instant nextAfterInst = null;

		// 다음 페이지가 존재할 때만 커서 값을 세팅하고, 없으면 null을 유지
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