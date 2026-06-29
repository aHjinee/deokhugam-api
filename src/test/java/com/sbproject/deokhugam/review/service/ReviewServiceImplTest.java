package com.sbproject.deokhugam.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
import com.sbproject.deokhugam.review.service.Impl.ReviewServiceImpl;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ReviewLikeRepository reviewLikeRepository;

	@InjectMocks
	private ReviewServiceImpl reviewService;

	// ==========================================
	// 1. create() 테스트 그룹
	// ==========================================

	@Test
	@DisplayName("리뷰 등록 성공: 올바른 요청 시 책 평점을 반영하고 리뷰를 저장한다")
	void createSuccess() {
		// given
		UUID bookId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "좋은 책입니다.", 5);

		Book book = mock(Book.class);
		User user = User.builder().id(userId).nickname("woody").build();

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(reviewRepository.existsByUserIdAndBookIdAndDeletedAtIsNotNull(userId, bookId)).thenReturn(false);
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
			Review review = invocation.getArgument(0);
			return review; // 빌더로 생성된 내부 값을 확인하기 위해 그대로 반환
		});

		// when
		ReviewDto result = reviewService.create(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEqualTo("좋은 책입니다.");
		assertThat(result.getRating()).isEqualTo(5);
		assertThat(result.getLikedByMe()).isFalse();
		verify(book).addReviewRating(5); // 도서 평점 추가 메서드 호출 검증

		ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
		verify(reviewRepository).save(reviewCaptor.capture());
		Review savedReview = reviewCaptor.getValue();
		assertThat(savedReview.getLikeCount()).isEqualTo(0);
		assertThat(savedReview.getCommentCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("리뷰 등록 실패: 도서 ID가 누락된 경우 IllegalArgumentException이 발생한다")
	void createThrowsWhenBookIdIsNull() {
		// given
		ReviewCreateRequest request = new ReviewCreateRequest(null, UUID.randomUUID(), "내용", 5);

		// when & then
		assertThatThrownBy(() -> reviewService.create(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Book id is null");
	}

	@Test
	@DisplayName("리뷰 등록 실패: 유저 ID가 누락된 경우 IllegalArgumentException이 발생한다")
	void createThrowsWhenUserIdIsNull() {
		// given
		ReviewCreateRequest request = new ReviewCreateRequest(UUID.randomUUID(), null, "내용", 5);

		// when & then
		assertThatThrownBy(() -> reviewService.create(request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("User id is null");
	}

	@Test
	@DisplayName("리뷰 등록 실패: 존재하지 않는 도서 ID일 경우 BookNotFoundException이 발생한다")
	void createThrowsWhenBookNotFound() {
		// given
		UUID bookId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "내용", 5);

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.create(request))
			.isInstanceOf(BookNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 등록 실패: 존재하지 않는 유저 ID일 경우 UserNotFoundException이 발생한다")
	void createThrowsWhenUserNotFound() {
		// given
		UUID bookId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "내용", 5);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(mock(Book.class)));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.create(request))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 등록 실패: 해당 유저가 해당 도서에 이미 작성 후 삭제되지 않은 리뷰가 있다면 ReviewAlreadyExistsException이 발생한다")
	void createThrowsWhenReviewAlreadyExists() {
		// given
		UUID bookId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "내용", 5);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(mock(Book.class)));
		when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().build()));
		when(reviewRepository.existsByUserIdAndBookIdAndDeletedAtIsNotNull(userId, bookId)).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> reviewService.create(request))
			.isInstanceOf(ReviewAlreadyExistsException.class);
	}


	// ==========================================
	// 2. update() 테스트 그룹
	// ==========================================

	@Test
	@DisplayName("리뷰 수정 성공: 요청자가 본인이고 평점이 변경되면 도서 평점을 업데이트한다")
	void updateSuccessWithRatingChange() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewUpdateRequest request = new ReviewUpdateRequest("수정된 내용", 3);

		User owner = User.builder().id(userId).build();
		Book book = mock(Book.class);
		Review review = Review.builder().id(reviewId).user(owner).book(book).content("기존 내용").rating(5).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(reviewLikeRepository.existsByUser_IdAndReview_Id(userId, reviewId)).thenReturn(true);

		// when
		ReviewDto result = reviewService.update(reviewId, userId, request);

		// then
		assertThat(result.getContent()).isEqualTo("수정된 내용");
		assertThat(result.getRating()).isEqualTo(3);
		assertThat(result.getLikedByMe()).isTrue();
		verify(book).updateReviewRating(5, 3); // 평점 변경 처리 검증
	}

	@Test
	@DisplayName("리뷰 수정 실패: 요청자 ID가 누락되면 IllegalArgumentException이 발생한다")
	void updateThrowsWhenUserIdIsNull() {
		// given
		UUID reviewId = UUID.randomUUID();
		ReviewUpdateRequest request = new ReviewUpdateRequest("내용", 4);

		// when & then
		assertThatThrownBy(() -> reviewService.update(reviewId, null, request))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("요청자 ID 누락");
	}

	@Test
	@DisplayName("리뷰 수정 실패: 존재하지 않는 리뷰 ID일 경우 ReviewNotFoundException이 발생한다")
	void updateThrowsWhenReviewNotFound() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewUpdateRequest request = new ReviewUpdateRequest("내용", 4);

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.update(reviewId, userId, request))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 수정 실패: 소프트 딜리트(deletedAt 존재)된 리뷰일 경우 ReviewNotFoundException이 발생한다")
	void updateThrowsWhenReviewIsSoftDeleted() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		ReviewUpdateRequest request = new ReviewUpdateRequest("내용", 4);

		Review review = Review.builder().id(reviewId).build();
		review.markDeleted(); // deletedAt 활성화 가정

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		assertThatThrownBy(() -> reviewService.update(reviewId, userId, request))
			.isInstanceOf(ReviewNotFoundException.class);
	}

	@Test
	@DisplayName("리뷰 수정 실패: 작성자가 아닌 다른 유저가 수정을 시도하면 ReviewNotOwnedException이 발생한다")
	void updateThrowsWhenReviewNotOwned() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		UUID requesterId = UUID.randomUUID();
		ReviewUpdateRequest request = new ReviewUpdateRequest("내용", 4);

		User owner = User.builder().id(ownerId).build();
		Review review = Review.builder().id(reviewId).user(owner).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		assertThatThrownBy(() -> reviewService.update(reviewId, requesterId, request))
			.isInstanceOf(ReviewNotOwnedException.class);
	}


	// ==========================================
	// 3. findById() 테스트 그룹
	// ==========================================

	@Test
	@DisplayName("리뷰 단건 조회 성공: 삭제되지 않은 리뷰 조회 시 DTO로 정상 반환되며 좋아요 여부도 함께 포함한다")
	void findByIdSuccess() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		User user = User.builder().id(UUID.randomUUID()).build();
		Book book = mock(Book.class);
		Review review = Review.builder().id(reviewId).user(user).book(book).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(reviewLikeRepository.existsByUser_IdAndReview_Id(userId, reviewId)).thenReturn(true);

		// when
		ReviewDto result = reviewService.findById(reviewId, userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getLikedByMe()).isTrue();
	}

	@Test
	@DisplayName("리뷰 단건 조회 실패: 요청자 ID가 없을 경우 IllegalArgumentException이 발생한다")
	void findByIdThrowsWhenUserIdIsNull() {
		// given
		UUID reviewId = UUID.randomUUID();

		// when & then
		assertThatThrownBy(() -> reviewService.findById(reviewId, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("요청자 ID 누락");
	}

	@Test
	@DisplayName("리뷰 단건 조회 실패: 소프트 삭제 처리된 리뷰 조회 시 ReviewNotFoundException이 발생한다")
	void findByIdThrowsWhenSoftDeleted() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		Review review = Review.builder().id(reviewId).build();
		review.markDeleted();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		assertThatThrownBy(() -> reviewService.findById(reviewId, userId))
			.isInstanceOf(ReviewNotFoundException.class);
	}


	// ==========================================
	// 4. delete() 테스트 그룹 (하드 딜리트)
	// ==========================================

	@Test
	@DisplayName("리뷰 완전 삭제 성공: 삭제 전 상태(deletedAt == null)라면 도서 평점을 차감하고 영구 삭제한다")
	void deleteHardSuccess() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		User owner = User.builder().id(userId).build();
		Book book = mock(Book.class);
		Review review = Review.builder().id(reviewId).user(owner).book(book).rating(4).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when
		reviewService.delete(reviewId, userId);

		// then
		verify(book).deleteReviewRating(4); // 평점 차감 동작 검증
		verify(reviewRepository).delete(review);
	}

	@Test
	@DisplayName("리뷰 완전 삭제 실패: 소유주가 아닐 경우 ReviewNotOwnedException이 발생한다")
	void deleteHardThrowsWhenNotOwned() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		UUID requesterId = UUID.randomUUID();

		User owner = User.builder().id(ownerId).build();
		Review review = Review.builder().id(reviewId).user(owner).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		assertThatThrownBy(() -> reviewService.delete(reviewId, requesterId))
			.isInstanceOf(ReviewNotOwnedException.class);
		verify(reviewRepository, never()).delete(any(Review.class));
	}


	// ==========================================
	// 5. deleteSoft() 테스트 그룹 (소프트 딜리트)
	// ==========================================

	@Test
	@DisplayName("리뷰 소프트 삭제 성공: 도서 평점을 차감하고 markDeleted 상태로 변경한다")
	void deleteSoftSuccess() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		User owner = User.builder().id(userId).build();
		Book book = mock(Book.class);
		Review review = Review.builder().id(reviewId).user(owner).book(book).rating(5).build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when
		reviewService.deleteSoft(reviewId, userId);

		// then
		verify(book).deleteReviewRating(5); // 평점 차감 검증
		assertThat(review.getDeletedAt()).isNotNull(); // 소프트 딜리트 마킹 상태 확인
	}

	@Test
	@DisplayName("리뷰 소프트 삭제 실패: 이미 삭제된 리뷰를 재삭제 시도 시 ReviewNotFoundException이 발생한다")
	void deleteSoftThrowsWhenAlreadyDeleted() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		Review review = Review.builder().id(reviewId).build();
		review.markDeleted(); // 이미 삭제 처리됨

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when & then
		assertThatThrownBy(() -> reviewService.deleteSoft(reviewId, userId))
			.isInstanceOf(ReviewNotFoundException.class);
	}


	// ==========================================
	// 6. findAll() 테스트 그룹
	// ==========================================

	@Test
	@DisplayName("리뷰 목록 검색 성공: 검색 조건에 맞춰 커서 기반 페이징 결과를 반환한다")
	void findAllSuccess() {
		// given
		ReviewSearchRequest request = new ReviewSearchRequest();
		SlicePageResponse<ReviewDto> expectedResponse = mock(SlicePageResponse.class);

		when(reviewRepository.searchReviewsCursorSorted(request)).thenReturn(expectedResponse);

		// when
		SlicePageResponse<ReviewDto> result = reviewService.findAll(request);

		// then
		assertThat(result).isEqualTo(expectedResponse);
		verify(reviewRepository).searchReviewsCursorSorted(request);
	}
}
