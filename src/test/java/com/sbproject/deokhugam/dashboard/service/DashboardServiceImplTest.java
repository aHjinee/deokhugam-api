package com.sbproject.deokhugam.dashboard.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import com.sbproject.deokhugam.dashboard.repository.PopularBooksRepository;
import com.sbproject.deokhugam.dashboard.repository.PopularReviewsRepository;
import com.sbproject.deokhugam.dashboard.repository.PowerUsersRepository;
import com.sbproject.deokhugam.dashboard.service.impl.DashboardServiceImpl;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

	@Mock
	private PopularBooksRepository popularBooksRepository;
	@Mock
	private PopularReviewsRepository popularReviewsRepository;
	@Mock
	private PowerUsersRepository powerUsersRepository;

	@InjectMocks
	private DashboardServiceImpl dashboardService;

	// ========== 인기 도서 ==========

	@Test
	@DisplayName("인기 도서 조회 - 데이터가 있으면 랭킹 목록을 반환")
	void getPopularBooks_success() {
		// given
		PopularBooksDocument.Ranking ranking = mock(PopularBooksDocument.Ranking.class);
		given(ranking.getRank()).willReturn(1);
		given(ranking.getBookId()).willReturn("book-uuid");
		given(ranking.getTitle()).willReturn("클린 코드");
		given(ranking.getAuthor()).willReturn("로버트 마틴");
		given(ranking.getThumbnailUrl()).willReturn("http://thumbnail.url");
		given(ranking.getScore()).willReturn(4.2);
		given(ranking.getReviewCount()).willReturn(10);
		given(ranking.getAvgRating()).willReturn(4.5);

		PopularBooksDocument doc = mock(PopularBooksDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.DAILY);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking));

		given(popularBooksRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.DAILY))
			.willReturn(Optional.of(doc));

		// when
		PopularBooksResponse response = dashboardService.getPopularBooks(PeriodType.DAILY, "ASC", 10);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getTitle()).isEqualTo("클린 코드");
		assertThat(response.getPeriodType()).isEqualTo(PeriodType.DAILY);
	}

	@Test
	@DisplayName("인기 도서 조회 - 데이터가 없으면 빈 리스트 반환")
	void getPopularBooks_empty() {
		// given
		given(popularBooksRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.DAILY))
			.willReturn(Optional.empty());

		// when
		PopularBooksResponse response = dashboardService.getPopularBooks(PeriodType.DAILY, "ASC", 10);

		// then
		assertThat(response.getContent()).isEmpty();
		assertThat(response.getPeriodType()).isEqualTo(PeriodType.DAILY);
		assertThat(response.getPeriodDate()).isNull();
	}

	@Test
	@DisplayName("인기 도서 조회 - DESC 정렬이면 rank 내림차순으로 반환")
	void getPopularBooks_descDirection() {
		// given
		PopularBooksDocument.Ranking ranking1 = mock(PopularBooksDocument.Ranking.class);
		given(ranking1.getRank()).willReturn(1);
		given(ranking1.getBookId()).willReturn("book-1");
		given(ranking1.getTitle()).willReturn("책1");
		given(ranking1.getAuthor()).willReturn("저자1");
		given(ranking1.getThumbnailUrl()).willReturn("url1");
		given(ranking1.getScore()).willReturn(4.0);
		given(ranking1.getReviewCount()).willReturn(5);
		given(ranking1.getAvgRating()).willReturn(4.0);

		PopularBooksDocument.Ranking ranking2 = mock(PopularBooksDocument.Ranking.class);
		given(ranking2.getRank()).willReturn(2);
		given(ranking2.getBookId()).willReturn("book-2");
		given(ranking2.getTitle()).willReturn("책2");
		given(ranking2.getAuthor()).willReturn("저자2");
		given(ranking2.getThumbnailUrl()).willReturn("url2");
		given(ranking2.getScore()).willReturn(3.0);
		given(ranking2.getReviewCount()).willReturn(3);
		given(ranking2.getAvgRating()).willReturn(3.0);

		PopularBooksDocument doc = mock(PopularBooksDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.DAILY);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking1, ranking2));

		given(popularBooksRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.DAILY))
			.willReturn(Optional.of(doc));

		// when
		PopularBooksResponse response = dashboardService.getPopularBooks(PeriodType.DAILY, "DESC", 10);

		// then
		assertThat(response.getContent().get(0).getRank()).isEqualTo(2);
		assertThat(response.getContent().get(1).getRank()).isEqualTo(1);
	}

	@Test
	@DisplayName("인기 도서 조회 - limit보다 데이터가 많으면 limit만큼만 반환")
	void getPopularBooks_limit() {
		// given
		PopularBooksDocument.Ranking ranking1 = mock(PopularBooksDocument.Ranking.class);
		given(ranking1.getRank()).willReturn(1);

		PopularBooksDocument.Ranking ranking2 = mock(PopularBooksDocument.Ranking.class);
		given(ranking2.getRank()).willReturn(2);

		PopularBooksDocument doc = mock(PopularBooksDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.DAILY);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking1, ranking2));

		given(popularBooksRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.DAILY))
			.willReturn(Optional.of(doc));

		// when
		PopularBooksResponse response = dashboardService.getPopularBooks(PeriodType.DAILY, "ASC", 1);

		// then
		assertThat(response.getContent()).hasSize(1);
	}

	// ========== 인기 리뷰 ==========

	@Test
	@DisplayName("인기 리뷰 조회 - 데이터가 있으면 랭킹 목록을 반환")
	void getPopularReviews_success() {
		// given
		PopularReviewsDocument.Ranking ranking = mock(PopularReviewsDocument.Ranking.class);
		given(ranking.getRank()).willReturn(1);
		given(ranking.getReviewId()).willReturn("review-uuid");
		given(ranking.getUserId()).willReturn("user-uuid");
		given(ranking.getBookId()).willReturn("book-uuid");
		given(ranking.getNickname()).willReturn("우디");
		given(ranking.getTitle()).willReturn("클린 코드");
		given(ranking.getThumbnailUrl()).willReturn("http://thumbnail.url");
		given(ranking.getContent()).willReturn("좋은 책입니다.");
		given(ranking.getRating()).willReturn(4.5);
		given(ranking.getScore()).willReturn(3.2);
		given(ranking.getLikeCount()).willReturn(5);
		given(ranking.getCommentCount()).willReturn(3);
		given(ranking.getCreatedAt()).willReturn(Instant.now());

		PopularReviewsDocument doc = mock(PopularReviewsDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.WEEKLY);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking));

		given(popularReviewsRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.WEEKLY))
			.willReturn(Optional.of(doc));

		// when
		PopularReviewsResponse response = dashboardService.getPopularReviews(PeriodType.WEEKLY, "ASC", 10);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getNickname()).isEqualTo("우디");
		assertThat(response.getPeriodType()).isEqualTo(PeriodType.WEEKLY);
	}

	@Test
	@DisplayName("인기 리뷰 조회 - 데이터가 없으면 빈 리스트 반환")
	void getPopularReviews_empty() {
		// given
		given(popularReviewsRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.MONTHLY))
			.willReturn(Optional.empty());

		// when
		PopularReviewsResponse response = dashboardService.getPopularReviews(PeriodType.MONTHLY, "ASC", 10);

		// then
		assertThat(response.getContent()).isEmpty();
		assertThat(response.getPeriodType()).isEqualTo(PeriodType.MONTHLY);
		assertThat(response.getPeriodDate()).isNull();
	}

	@Test
	@DisplayName("인기 리뷰 조회 - DESC 정렬이면 score 높은 순으로 반환")
	void getPopularReviews_descDirection() {
		// given
		PopularReviewsDocument.Ranking ranking1 = mock(PopularReviewsDocument.Ranking.class);
		given(ranking1.getRank()).willReturn(1);
		given(ranking1.getReviewId()).willReturn("review-1");
		given(ranking1.getUserId()).willReturn("user-1");
		given(ranking1.getBookId()).willReturn("book-1");
		given(ranking1.getNickname()).willReturn("우디");
		given(ranking1.getTitle()).willReturn("책1");
		given(ranking1.getThumbnailUrl()).willReturn("url1");
		given(ranking1.getContent()).willReturn("내용1");
		given(ranking1.getRating()).willReturn(4.5);
		given(ranking1.getScore()).willReturn(4.0);
		given(ranking1.getLikeCount()).willReturn(5);
		given(ranking1.getCommentCount()).willReturn(3);
		given(ranking1.getCreatedAt()).willReturn(Instant.now());

		PopularReviewsDocument.Ranking ranking2 = mock(PopularReviewsDocument.Ranking.class);
		given(ranking2.getRank()).willReturn(2);
		given(ranking2.getReviewId()).willReturn("review-2");
		given(ranking2.getUserId()).willReturn("user-2");
		given(ranking2.getBookId()).willReturn("book-2");
		given(ranking2.getNickname()).willReturn("버즈");
		given(ranking2.getTitle()).willReturn("책2");
		given(ranking2.getThumbnailUrl()).willReturn("url2");
		given(ranking2.getContent()).willReturn("내용2");
		given(ranking2.getRating()).willReturn(3.5);
		given(ranking2.getScore()).willReturn(2.0);
		given(ranking2.getLikeCount()).willReturn(3);
		given(ranking2.getCommentCount()).willReturn(1);
		given(ranking2.getCreatedAt()).willReturn(Instant.now());

		PopularReviewsDocument doc = mock(PopularReviewsDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.WEEKLY);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking1, ranking2));

		given(popularReviewsRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.WEEKLY))
			.willReturn(Optional.of(doc));

		// when
		PopularReviewsResponse response = dashboardService.getPopularReviews(PeriodType.WEEKLY, "DESC", 10);

		// then
		assertThat(response.getContent().get(0).getScore()).isEqualTo(4.0);
		assertThat(response.getContent().get(1).getScore()).isEqualTo(2.0);
	}

	@Test
	@DisplayName("인기 리뷰 조회 - limit보다 데이터가 많으면 limit만큼만 반환")
	void getPopularReviews_limit() {
		// given
		PopularReviewsDocument.Ranking ranking1 = mock(PopularReviewsDocument.Ranking.class);
		given(ranking1.getScore()).willReturn(4.0);

		PopularReviewsDocument.Ranking ranking2 = mock(PopularReviewsDocument.Ranking.class);
		given(ranking2.getScore()).willReturn(2.0);

		PopularReviewsDocument doc = mock(PopularReviewsDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.WEEKLY);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking1, ranking2));

		given(popularReviewsRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.WEEKLY))
			.willReturn(Optional.of(doc));

		// when
		PopularReviewsResponse response = dashboardService.getPopularReviews(PeriodType.WEEKLY, "ASC", 1);

		// then
		assertThat(response.getContent()).hasSize(1);
	}

	// ========== 파워 유저 ==========

	@Test
	@DisplayName("파워 유저 조회 - 데이터가 있으면 랭킹 목록을 반환")
	void getPowerUsers_success() {
		// given
		PowerUsersDocument.Ranking ranking = mock(PowerUsersDocument.Ranking.class);
		given(ranking.getRank()).willReturn(1);
		given(ranking.getUserId()).willReturn("user-uuid");
		given(ranking.getNickname()).willReturn("버즈");
		given(ranking.getActivityScore()).willReturn(8.5);
		given(ranking.getReviewScore()).willReturn(5.0);
		given(ranking.getLikeCount()).willReturn(10);
		given(ranking.getCommentCount()).willReturn(7);

		PowerUsersDocument doc = mock(PowerUsersDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.ALL_TIME);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking));

		given(powerUsersRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.ALL_TIME))
			.willReturn(Optional.of(doc));

		// when
		PowerUsersResponse response = dashboardService.getPowerUsers(PeriodType.ALL_TIME, "ASC", 10);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getNickname()).isEqualTo("버즈");
		assertThat(response.getPeriodType()).isEqualTo(PeriodType.ALL_TIME);
	}

	@Test
	@DisplayName("파워 유저 조회 - 데이터가 없으면 빈 리스트 반환")
	void getPowerUsers_empty() {
		// given
		given(powerUsersRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.ALL_TIME))
			.willReturn(Optional.empty());

		// when
		PowerUsersResponse response = dashboardService.getPowerUsers(PeriodType.ALL_TIME, "ASC", 10);

		// then
		assertThat(response.getContent()).isEmpty();
		assertThat(response.getPeriodType()).isEqualTo(PeriodType.ALL_TIME);
		assertThat(response.getPeriodDate()).isNull();
	}

	@Test
	@DisplayName("파워 유저 조회 - DESC 정렬이면 rank 내림차순으로 반환")
	void getPowerUsers_descDirection() {
		// given
		PowerUsersDocument.Ranking ranking1 = mock(PowerUsersDocument.Ranking.class);
		given(ranking1.getRank()).willReturn(1);
		given(ranking1.getUserId()).willReturn("user-1");
		given(ranking1.getNickname()).willReturn("우디");
		given(ranking1.getActivityScore()).willReturn(8.5);
		given(ranking1.getReviewScore()).willReturn(5.0);
		given(ranking1.getLikeCount()).willReturn(10);
		given(ranking1.getCommentCount()).willReturn(7);

		PowerUsersDocument.Ranking ranking2 = mock(PowerUsersDocument.Ranking.class);
		given(ranking2.getRank()).willReturn(2);
		given(ranking2.getUserId()).willReturn("user-2");
		given(ranking2.getNickname()).willReturn("버즈");
		given(ranking2.getActivityScore()).willReturn(5.0);
		given(ranking2.getReviewScore()).willReturn(3.0);
		given(ranking2.getLikeCount()).willReturn(5);
		given(ranking2.getCommentCount()).willReturn(3);

		PowerUsersDocument doc = mock(PowerUsersDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.ALL_TIME);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking1, ranking2));

		given(powerUsersRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.ALL_TIME))
			.willReturn(Optional.of(doc));

		// when
		PowerUsersResponse response = dashboardService.getPowerUsers(PeriodType.ALL_TIME, "DESC", 10);

		// then
		assertThat(response.getContent().get(0).getRank()).isEqualTo(2);
		assertThat(response.getContent().get(1).getRank()).isEqualTo(1);
	}

	@Test
	@DisplayName("파워 유저 조회 - limit보다 데이터가 많으면 limit만큼만 반환")
	void getPowerUsers_limit() {
		// given
		PowerUsersDocument.Ranking ranking1 = mock(PowerUsersDocument.Ranking.class);
		given(ranking1.getRank()).willReturn(1);

		PowerUsersDocument.Ranking ranking2 = mock(PowerUsersDocument.Ranking.class);
		given(ranking2.getRank()).willReturn(2);

		PowerUsersDocument doc = mock(PowerUsersDocument.class);
		given(doc.getPeriodType()).willReturn(PeriodType.ALL_TIME);
		given(doc.getPeriodDate()).willReturn(Instant.now());
		given(doc.getRankings()).willReturn(List.of(ranking1, ranking2));

		given(powerUsersRepository.findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.ALL_TIME))
			.willReturn(Optional.of(doc));

		// when
		PowerUsersResponse response = dashboardService.getPowerUsers(PeriodType.ALL_TIME, "ASC", 1);

		// then
		assertThat(response.getContent()).hasSize(1);
	}
}