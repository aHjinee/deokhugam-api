package com.sbproject.deokhugam.review.service.Impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.exception.BookNotFoundException;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewCreateRequest;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;
import com.sbproject.deokhugam.review.dto.ReviewUpdateRequest;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.exception.ReviewAlreadyExistsException;
import com.sbproject.deokhugam.review.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.review.exception.ReviewNotOwnedException;
import com.sbproject.deokhugam.review.repository.ReviewLikeRepository;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.review.service.ReviewService;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;
	private final ReviewLikeRepository reviewLikeRepository;

	@Override
	@Transactional
	public ReviewDto create(ReviewCreateRequest request) {
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> BookNotFoundException.withId(request.getBookId()));

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> UserNotFoundException.withId(request.getUserId()));

		if (reviewRepository.existsByUserIdAndBookIdAndDeletedAtIsNotNull(request.getUserId(), request.getBookId())) {
			throw ReviewAlreadyExistsException.withIds(request.getUserId(), request.getBookId());
		}

		Integer rating = request.getRating();

		book.addReviewRating(rating);

		Review review = Review.builder()
			.book(book)
			.user(user)
			.content(request.getContent())
			.rating(request.getRating())
			.likeCount(0)
			.commentCount(0)
			.build();

		Review savedReview = reviewRepository.save(review);

		return convertToDto(savedReview, false);
	}

	@Override
	@Transactional
	public ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request) {
		if (userId == null) {
			throw new IllegalArgumentException("요청자 ID 누락");
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

		if(review.getDeletedAt() != null) {
			throw ReviewNotFoundException.withId(reviewId);
		}

		if (!review.getUser().getId().equals(userId)) {
			throw ReviewNotOwnedException.withId(reviewId, userId);
		}

		Integer oldRating = review.getRating();
		Integer newRating = request.getRating();

		if (newRating != null && !oldRating.equals(newRating)) {
			review.getBook().updateReviewRating(oldRating, newRating);
			review.setRating(newRating);
		}

		if (request.getContent() != null) {
			review.setContent(request.getContent());
		}

		boolean likedByMe = reviewLikeRepository.existsByUser_IdAndReview_Id(userId, reviewId);

		return convertToDto(review, likedByMe);
	}

	@Override
	public ReviewDto findById(UUID reviewId, UUID userId) {
		if (userId == null) {
			throw new IllegalArgumentException("요청자 ID 누락");
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

		if (review.getDeletedAt() != null) {
			throw ReviewNotFoundException.withId(reviewId);
		}

		boolean likedByMe = false;
		if (userId != null) {
			likedByMe = reviewLikeRepository.existsByUser_IdAndReview_Id(userId, reviewId);
		}

		return convertToDto(review, likedByMe);
	}


	@Override
	@Transactional
	public void delete(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

		if (!review.getUser().getId().equals(userId)) {
			throw ReviewNotOwnedException.withId(reviewId, userId);
		}

		if(review.getDeletedAt() == null) {
			review.getBook().deleteReviewRating(review.getRating());
		}

		reviewRepository.delete(review);
	}

	@Override
	@Transactional
	public void deleteSoft(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

		if (review.getDeletedAt() != null) {
			throw ReviewNotFoundException.withId(reviewId);
		}

		if (!review.getUser().getId().equals(userId)) {
			throw ReviewNotOwnedException.withId(reviewId, userId);
		}

		review.getBook().deleteReviewRating(review.getRating());

		review.markDeleted();
	}

	@Override
	public SlicePageResponse<ReviewDto> findAll(ReviewSearchRequest request) {
		return reviewRepository.searchReviewsCursorSorted(request);
	}

	private ReviewDto convertToDto(Review review, boolean likedByMe) {
		return ReviewDto.builder()
			.id(review.getId())
			.bookId(review.getBook().getId())
			.bookTitle(review.getBook().getTitle())
			.bookThumbnailUrl(review.getBook().getThumbnailUrl())
			.userId(review.getUser().getId())
			.userNickname(review.getUser().getNickname())
			.content(review.getContent())
			.rating(review.getRating())
			.likeCount(review.getLikeCount())
			.commentCount(review.getCommentCount())
			.likedByMe(likedByMe)
			.createdAt(review.getCreatedAt())
			.updatedAt(review.getUpdatedAt())
			.build();
	}
}
