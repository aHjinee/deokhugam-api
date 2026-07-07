package com.sbproject.deokhugam.book.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbproject.deokhugam.book.dto.BookOrderBy;
import com.sbproject.deokhugam.book.dto.Direction;
import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.entity.QBook;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookQueryRepositoryImpl implements BookQueryRepository {

	private static final QBook book = QBook.book;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Slice<Book> searchBooks(String keyword, BookOrderBy orderBy, Direction direction, String cursor,
	                               Instant after, int limit) {
		BooleanBuilder where = new BooleanBuilder();
		where.and(book.deletedAt.isNull());
		if (keyword != null && !keyword.isBlank()) {
			String normalized = keyword.toLowerCase().replace(" ", "");
			BooleanExpression titleMatch = Expressions.booleanTemplate(
				"replace(lower({0}), ' ', '') like concat('%', {1}, '%')", book.title, normalized);
			BooleanExpression authorMatch = Expressions.booleanTemplate(
				"replace(lower({0}), ' ', '') like concat('%', {1}, '%')", book.author, normalized);
			BooleanExpression titleSimilar = Expressions.booleanTemplate(
				"function('bigm_similar', replace(lower({0}), ' ', ''), {1}) = true", book.title, normalized);
			BooleanExpression authorSimilar = Expressions.booleanTemplate(
				"function('bigm_similar', replace(lower({0}), ' ', ''), {1}) = true", book.author, normalized);
			where.and(
				titleMatch
					.or(authorMatch)
					.or(titleSimilar)
					.or(authorSimilar)
					.or(book.isbn.contains(keyword))
			);
		}
		boolean desc = Direction.DESC.equals(direction);
		if (cursor != null) {
			BooleanExpression primary = switch (orderBy) {
				case title -> desc ? book.title.lt(cursor) : book.title.gt(cursor);
				case publishedDate -> desc ? book.publishedDate.lt(LocalDate.parse(cursor))
					: book.publishedDate.gt(LocalDate.parse(cursor));
				case rating -> desc ? book.rating.lt(Double.parseDouble(cursor))
					: book.rating.gt(Double.parseDouble(cursor));
				case reviewCount -> desc ? book.reviewCount.lt(Integer.parseInt(cursor))
					: book.reviewCount.gt(Integer.parseInt(cursor));
			};

			if (after != null) {
				BooleanExpression equal = switch (orderBy) {
					case title -> book.title.eq(cursor);
					case publishedDate -> book.publishedDate.eq(LocalDate.parse(cursor));
					case rating -> book.rating.eq(Double.parseDouble(cursor));
					case reviewCount -> book.reviewCount.eq(Integer.parseInt(cursor));
				};
				BooleanExpression tieBreak = desc ? book.createdAt.lt(after) : book.createdAt.gt(after);
				where.and(primary.or(equal.and(tieBreak)));
			} else {
				where.and(primary);
			}
		}

		OrderSpecifier<?> order = switch (orderBy) {
			case title -> desc ? book.title.desc() : book.title.asc();
			case publishedDate -> desc ? book.publishedDate.desc() : book.publishedDate.asc();
			case rating -> desc ? book.rating.desc() : book.rating.asc();
			case reviewCount -> desc ? book.reviewCount.desc() : book.reviewCount.asc();
		};

		OrderSpecifier<?> createdAtOrder = desc ? book.createdAt.desc() : book.createdAt.asc();

		List<Book> books = jpaQueryFactory.selectFrom(book)
		                                  .where(where)
		                                  .orderBy(order, createdAtOrder)
		                                  .limit(limit + 1)
		                                  .fetch();

		boolean hasNext = books.size() > limit;
		List<Book> content = hasNext ? books.subList(0, limit) : books;
		return new SliceImpl<>(content, PageRequest.ofSize(limit), hasNext);
	}
}
