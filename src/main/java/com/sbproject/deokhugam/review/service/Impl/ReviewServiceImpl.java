package com.sbproject.deokhugam.review.service.Impl;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.book.repository.BookRepository;
import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.review.dto.ReviewCreateRequest;
import com.sbproject.deokhugam.review.dto.ReviewDto;
import com.sbproject.deokhugam.review.dto.ReviewSearchRequest;
import com.sbproject.deokhugam.review.dto.ReviewUpdateRequest;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.repository.ReviewLikeRepository;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.review.service.ReviewService;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import de.codecentric.boot.admin.client.registration.ApplicationRegistrator;
import lombok.RequiredArgsConstructor;

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
		if (request.getBookId() == null) {
			throw new IllegalArgumentException("Book id is null");
		}
		if (request.getUserId() == null) {
			throw new IllegalArgumentException("User id is null");
		}

		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 도서입니다. ID: " + request.getBookId()));

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> UserNotFoundException.withId(request.getUserId()));

		Integer rating = request.getRating();

		// 도서(Book)의 리뷰 개수 및 평균 평점 업데이트 (도서 엔티티의 비즈니스 메서드 호출)
		book.addReviewRating(rating);

		Review review = Review.builder()
			.book(book)
			.user(user)
			.content(request.getContent())
			.rating(request.getRating())
			.likeCount(0)       // 생성자 단계 기본값 보장
			.commentCount(0)    // 생성자 단계 기본값 보장
			.build();

		Review savedReview = reviewRepository.save(review);

		return convertToDto(savedReview, false);
	}

	@Override
	public ReviewDto update(UUID reviewID, UUID userId, ReviewUpdateRequest request) {
		return null;
	}

	@Override
	public ReviewDto findById(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다. id: " + reviewId));

		// 2. 로그인한 유저의 좋아요 여부 확인
		boolean likedByMe = false;
		if (userId != null) {
			likedByMe = reviewLikeRepository.existsByUser_IdAndReview_Id(userId, reviewId);
		}

		// 3. Entity -> DTO 변환 및 반환
		return convertToDto(review, likedByMe);
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

	@Override
	public void delete(UUID reviewID, UUID userId) {

	}

	@Override
	public void deleteSoft(UUID reviewID, UUID userId) {

	}

	@Override
	public SlicePageResponse<ReviewDto> findAll(ReviewSearchRequest request) {
		return reviewRepository.searchReviewsCursorSorted(request);
	}
}
