package com.sbproject.deokhugam.review.repository.querydsl;

import java.time.Instant;
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

	private final JPAQueryFactory jpaQueryFactory;

	private static final QReview r = QReview.review;
	private static final QUser u = QUser.user;
	private static final QBook b = QBook.book;
	private static final QReviewLike l = QReviewLike.reviewLike;

	private static final QUser reviewAuthor  = new QUser("u2"); // 별칭 AS u2
	private static final QUser commentAuthor = new QUser("u3");

	@Override
	public SlicePageResponse<ReviewDto> searchCursorSorted(ReviewSearchRequest request) {
		BooleanBuilder where  = new BooleanBuilder();

		// soft delete 제외
		where.and(r.deletedAt.isNull());

		// 동적 쿼리 검색

		return null;
	}
}
