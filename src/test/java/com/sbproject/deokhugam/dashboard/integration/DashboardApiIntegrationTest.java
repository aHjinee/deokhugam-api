package com.sbproject.deokhugam.dashboard.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.document.UserActivityStatsDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import com.sbproject.deokhugam.dashboard.repository.PopularBooksRepository;
import com.sbproject.deokhugam.dashboard.repository.PopularReviewsRepository;
import com.sbproject.deokhugam.dashboard.repository.PowerUsersRepository;
import com.sbproject.deokhugam.dashboard.repository.UserActivityStatsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardApiIntegrationTest {

	private static final String BOOK_ID_1 =
		"0194263e-3a80-7972-8208-ba3c6c031199";
	private static final String BOOK_ID_2 =
		"0194263e-3a80-7972-8208-ba3c6c031200";

	private static final String REVIEW_ID_1 =
		"0194263e-3a80-7972-8208-ba3c6c031201";
	private static final String REVIEW_ID_2 =
		"0194263e-3a80-7972-8208-ba3c6c031202";

	private static final String USER_ID_1 =
		"0194263e-3a80-7972-8208-ba3c6c031203";
	private static final String USER_ID_2 =
		"0194263e-3a80-7972-8208-ba3c6c031204";

	private static final Instant PERIOD_DATE =
		Instant.parse("2026-07-01T00:00:00Z");
	private static final Instant CREATED_AT =
		Instant.parse("2026-07-01T01:00:00Z");

	private static final ZoneId SEOUL_ZONE =
		ZoneId.of("Asia/Seoul");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PopularBooksRepository popularBooksRepository;

	@Autowired
	private PopularReviewsRepository popularReviewsRepository;

	@Autowired
	private PowerUsersRepository powerUsersRepository;

	@Autowired
	private UserActivityStatsRepository userActivityStatsRepository;

	@BeforeEach
	void setUp() {
		clearRepositories();
	}

	private void clearRepositories() {
		popularBooksRepository.deleteAll();
		popularReviewsRepository.deleteAll();
		powerUsersRepository.deleteAll();
		userActivityStatsRepository.deleteAll();
	}

	// ---------- 인기 도서 GET /api/books/popular ----------

	@Test
	@DisplayName("인기 도서 조회 - 저장된 랭킹을 반환한다")
	void getPopularBooks_success() throws Exception {
		popularBooksRepository.save(popularBooksDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularBookRanking(
					1,
					BOOK_ID_1,
					"채식주의자",
					"한강",
					"https://example.com/book1.jpg",
					4.8,
					12,
					4.7
				),
				popularBookRanking(
					2,
					BOOK_ID_2,
					"소년이 온다",
					"한강",
					"https://example.com/book2.jpg",
					4.5,
					10,
					4.5
				)
			)
		));

		mockMvc.perform(get("/api/books/popular"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("ALL_TIME"))
			.andExpect(jsonPath("$.periodDate").value(PERIOD_DATE.toString()))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].id").value(BOOK_ID_1))
			.andExpect(jsonPath("$.content[0].bookId").value(BOOK_ID_1))
			.andExpect(jsonPath("$.content[0].title").value("채식주의자"))
			.andExpect(jsonPath("$.content[0].author").value("한강"))
			.andExpect(jsonPath("$.content[0].period").value("ALL_TIME"))
			.andExpect(jsonPath("$.content[0].rank").value(1))
			.andExpect(jsonPath("$.content[0].score").value(4.8))
			.andExpect(jsonPath("$.content[0].reviewCount").value(12))
			.andExpect(jsonPath("$.content[0].rating").value(4.7));
	}

	@Test
	@DisplayName("인기 도서 조회 - period에 맞는 최신 문서를 반환한다")
	void getPopularBooks_period() throws Exception {
		popularBooksRepository.save(popularBooksDocument(
			PeriodType.DAILY,
			List.of(
				popularBookRanking(
					1,
					BOOK_ID_1,
					"일간 인기 도서",
					"테스트 저자",
					null,
					10.0,
					5,
					4.0
				)
			)
		));

		popularBooksRepository.save(popularBooksDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularBookRanking(
					1,
					BOOK_ID_2,
					"역대 인기 도서",
					"테스트 저자",
					null,
					20.0,
					10,
					5.0
				)
			)
		));

		mockMvc.perform(get("/api/books/popular")
				.param("period", "DAILY"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("DAILY"))
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].title").value("일간 인기 도서"));
	}

	@Test
	@DisplayName("인기 도서 조회 - limit만큼 반환한다")
	void getPopularBooks_limit() throws Exception {
		popularBooksRepository.save(popularBooksDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularBookRanking(
					1, BOOK_ID_1, "도서 1", "저자 1",
					null, 10.0, 10, 5.0
				),
				popularBookRanking(
					2, BOOK_ID_2, "도서 2", "저자 2",
					null, 9.0, 9, 4.5
				)
			)
		));

		mockMvc.perform(get("/api/books/popular")
				.param("limit", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].rank").value(1));
	}

	@Test
	@DisplayName("인기 도서 조회 - direction이 DESC이면 순위를 역순으로 반환한다")
	void getPopularBooks_descDirection() throws Exception {
		popularBooksRepository.save(popularBooksDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularBookRanking(
					1, BOOK_ID_1, "도서 1", "저자 1",
					null, 10.0, 10, 5.0
				),
				popularBookRanking(
					2, BOOK_ID_2, "도서 2", "저자 2",
					null, 9.0, 9, 4.5
				)
			)
		));

		mockMvc.perform(get("/api/books/popular")
				.param("direction", "DESC"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].rank").value(2))
			.andExpect(jsonPath("$.content[1].rank").value(1));
	}

	@Test
	@DisplayName("인기 도서 조회 - 데이터가 없으면 빈 목록을 반환한다")
	void getPopularBooks_empty() throws Exception {
		mockMvc.perform(get("/api/books/popular")
				.param("period", "WEEKLY"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("WEEKLY"))
			.andExpect(jsonPath("$.periodDate").doesNotExist())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@DisplayName("인기 도서 조회 - limit이 1 미만이면 400")
	void getPopularBooks_invalidMinimumLimit() throws Exception {
		mockMvc.perform(get("/api/books/popular")
				.param("limit", "0"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인기 도서 조회 - limit이 4를 초과하면 400")
	void getPopularBooks_invalidMaximumLimit() throws Exception {
		mockMvc.perform(get("/api/books/popular")
				.param("limit", "5"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인기 도서 조회 - 잘못된 period이면 400")
	void getPopularBooks_invalidPeriod() throws Exception {
		mockMvc.perform(get("/api/books/popular")
				.param("period", "YEARLY"))
			.andExpect(status().isBadRequest());
	}

	// ---------- 인기 리뷰 GET /api/reviews/popular ----------

	@Test
	@DisplayName("인기 리뷰 조회 - 저장된 랭킹을 반환한다")
	void getPopularReviews_success() throws Exception {
		popularReviewsRepository.save(popularReviewsDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularReviewRanking(
					1,
					REVIEW_ID_1,
					USER_ID_1,
					BOOK_ID_1,
					"테스트 사용자",
					"채식주의자",
					"https://example.com/book1.jpg",
					"인상 깊은 리뷰입니다.",
					4.5,
					10.0,
					7,
					3,
					Instant.parse("2026-06-30T10:00:00Z")
				)
			)
		));

		mockMvc.perform(get("/api/reviews/popular"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("ALL_TIME"))
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].id").value(REVIEW_ID_1))
			.andExpect(jsonPath("$.content[0].reviewId").value(REVIEW_ID_1))
			.andExpect(jsonPath("$.content[0].bookId").value(BOOK_ID_1))
			.andExpect(jsonPath("$.content[0].bookTitle").value("채식주의자"))
			.andExpect(jsonPath("$.content[0].userId").value(USER_ID_1))
			.andExpect(jsonPath("$.content[0].userNickname").value("테스트 사용자"))
			.andExpect(jsonPath("$.content[0].reviewContent").value("인상 깊은 리뷰입니다."))
			.andExpect(jsonPath("$.content[0].reviewRating").value(4.5))
			.andExpect(jsonPath("$.content[0].rank").value(1))
			.andExpect(jsonPath("$.content[0].score").value(10.0))
			.andExpect(jsonPath("$.content[0].likeCount").value(7))
			.andExpect(jsonPath("$.content[0].commentCount").value(3));
	}

	@Test
	@DisplayName("인기 리뷰 조회 - direction이 ASC이면 점수가 낮은 순서로 반환한다")
	void getPopularReviews_ascDirection() throws Exception {
		popularReviewsRepository.save(popularReviewsDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularReviewRanking(
					1, REVIEW_ID_1, USER_ID_1, BOOK_ID_1,
					"사용자 1", "도서 1", null, "리뷰 1",
					5.0, 20.0, 10, 5, CREATED_AT
				),
				popularReviewRanking(
					2, REVIEW_ID_2, USER_ID_2, BOOK_ID_2,
					"사용자 2", "도서 2", null, "리뷰 2",
					4.0, 10.0, 5, 2, CREATED_AT
				)
			)
		));

		mockMvc.perform(get("/api/reviews/popular")
				.param("direction", "ASC"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].reviewId").value(REVIEW_ID_2))
			.andExpect(jsonPath("$.content[0].score").value(10.0))
			.andExpect(jsonPath("$.content[1].reviewId").value(REVIEW_ID_1))
			.andExpect(jsonPath("$.content[1].score").value(20.0));
	}

	@Test
	@DisplayName("인기 리뷰 조회 - 기본 direction은 DESC로 점수가 높은 순서로 반환한다")
	void getPopularReviews_defaultDirection() throws Exception {
		popularReviewsRepository.save(popularReviewsDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularReviewRanking(
					2, REVIEW_ID_2, USER_ID_2, BOOK_ID_2,
					"사용자 2", "도서 2", null, "리뷰 2",
					4.0, 10.0, 5, 2, CREATED_AT
				),
				popularReviewRanking(
					1, REVIEW_ID_1, USER_ID_1, BOOK_ID_1,
					"사용자 1", "도서 1", null, "리뷰 1",
					5.0, 20.0, 10, 5, CREATED_AT
				)
			)
		));

		mockMvc.perform(get("/api/reviews/popular"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].reviewId").value(REVIEW_ID_1))
			.andExpect(jsonPath("$.content[0].score").value(20.0))
			.andExpect(jsonPath("$.content[1].reviewId").value(REVIEW_ID_2))
			.andExpect(jsonPath("$.content[1].score").value(10.0));
	}

	@Test
	@DisplayName("인기 리뷰 조회 - limit만큼 반환한다")
	void getPopularReviews_limit() throws Exception {
		popularReviewsRepository.save(popularReviewsDocument(
			PeriodType.ALL_TIME,
			List.of(
				popularReviewRanking(
					1, REVIEW_ID_1, USER_ID_1, BOOK_ID_1,
					"사용자 1", "도서 1", null, "리뷰 1",
					5.0, 20.0, 10, 5, CREATED_AT
				),
				popularReviewRanking(
					2, REVIEW_ID_2, USER_ID_2, BOOK_ID_2,
					"사용자 2", "도서 2", null, "리뷰 2",
					4.0, 10.0, 5, 2, CREATED_AT
				)
			)
		));

		mockMvc.perform(get("/api/reviews/popular")
				.param("limit", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1));
	}

	@Test
	@DisplayName("인기 리뷰 조회 - 데이터가 없으면 빈 목록을 반환한다")
	void getPopularReviews_empty() throws Exception {
		mockMvc.perform(get("/api/reviews/popular")
				.param("period", "MONTHLY"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("MONTHLY"))
			.andExpect(jsonPath("$.periodDate").doesNotExist())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@DisplayName("인기 리뷰 조회 - limit이 1 미만이면 400")
	void getPopularReviews_invalidMinimumLimit() throws Exception {
		mockMvc.perform(get("/api/reviews/popular")
				.param("limit", "0"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("인기 리뷰 조회 - limit이 20을 초과하면 400")
	void getPopularReviews_invalidMaximumLimit() throws Exception {
		mockMvc.perform(get("/api/reviews/popular")
				.param("limit", "21"))
			.andExpect(status().isBadRequest());
	}

	// ---------- 파워 유저 GET /api/users/power ----------

	@Test
	@DisplayName("파워 유저 조회 - 저장된 역대 랭킹을 반환한다")
	void getPowerUsers_success() throws Exception {
		powerUsersRepository.save(powerUsersDocument(
			PeriodType.ALL_TIME,
			List.of(
				powerUserRanking(
					1,
					USER_ID_1,
					"파워 사용자",
					100.0,
					50.0,
					20,
					10
				)
			)
		));

		mockMvc.perform(get("/api/users/power"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("ALL_TIME"))
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].rank").value(1))
			.andExpect(jsonPath("$.content[0].userId").value(USER_ID_1))
			.andExpect(jsonPath("$.content[0].nickname").value("파워 사용자"))
			.andExpect(jsonPath("$.content[0].period").value("ALL_TIME"))
			.andExpect(jsonPath("$.content[0].score").value(100.0))
			.andExpect(jsonPath("$.content[0].reviewScoreSum").value(50.0))
			.andExpect(jsonPath("$.content[0].likeCount").value(20))
			.andExpect(jsonPath("$.content[0].commentCount").value(10));
	}

	@Test
	@DisplayName("파워 유저 조회 - direction이 DESC이면 순위를 역순으로 반환한다")
	void getPowerUsers_descDirection() throws Exception {
		powerUsersRepository.save(powerUsersDocument(
			PeriodType.ALL_TIME,
			List.of(
				powerUserRanking(
					1, USER_ID_1, "사용자 1",
					100.0, 50.0, 20, 10
				),
				powerUserRanking(
					2, USER_ID_2, "사용자 2",
					90.0, 40.0, 15, 8
				)
			)
		));

		mockMvc.perform(get("/api/users/power")
				.param("direction", "DESC"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].rank").value(2))
			.andExpect(jsonPath("$.content[1].rank").value(1));
	}

	@Test
	@DisplayName("파워 유저 조회 - limit만큼 반환한다")
	void getPowerUsers_limit() throws Exception {
		powerUsersRepository.save(powerUsersDocument(
			PeriodType.ALL_TIME,
			List.of(
				powerUserRanking(
					1, USER_ID_1, "사용자 1",
					100.0, 50.0, 20, 10
				),
				powerUserRanking(
					2, USER_ID_2, "사용자 2",
					90.0, 40.0, 15, 8
				)
			)
		));

		mockMvc.perform(get("/api/users/power")
				.param("limit", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].rank").value(1));
	}

	@Test
	@DisplayName("파워 유저 조회 - 데이터가 없으면 ALL_TIME 빈 목록을 반환한다")
	void getPowerUsers_empty() throws Exception {
		mockMvc.perform(get("/api/users/power"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.periodType").value("ALL_TIME"))
			.andExpect(jsonPath("$.periodDate").doesNotExist())
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@DisplayName("파워 유저 조회 - limit이 1 미만이면 400")
	void getPowerUsers_invalidMinimumLimit() throws Exception {
		mockMvc.perform(get("/api/users/power")
				.param("limit", "0"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("파워 유저 조회 - limit이 10을 초과하면 400")
	void getPowerUsers_invalidMaximumLimit() throws Exception {
		mockMvc.perform(get("/api/users/power")
				.param("limit", "11"))
			.andExpect(status().isBadRequest());
	}

	// ---------- 사용자 활동 통계 GET /api/users/{userId}/activity-stats ----------

	@Test
	@DisplayName("사용자 활동 통계 조회 - 현재 30일 주기를 날짜 오름차순으로 반환한다")
	void getUserActivityStats_success() throws Exception {
		LocalDate today = LocalDate.now(SEOUL_ZONE);
		LocalDate firstActivityDate = today.minusDays(1);

		Instant firstActivityInstant =
			firstActivityDate
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		Instant todayInstant =
			today
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_1,
			firstActivityInstant,
			2,
			3,
			4,
			5,
			6,
			2
		));

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_1,
			todayInstant,
			7,
			8,
			9,
			10,
			11,
			1
		));

		mockMvc.perform(get(
				"/api/users/{userId}/activity-stats",
				USER_ID_1
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(USER_ID_1))
			.andExpect(jsonPath("$.content.length()").value(30))

			// 최초 활동일
			.andExpect(jsonPath("$.content[0].activityDate")
				.value(firstActivityInstant.toString()))
			.andExpect(jsonPath("$.content[0].reviewCount").value(2))
			.andExpect(jsonPath("$.content[0].commentCount").value(3))
			.andExpect(jsonPath("$.content[0].likeCount").value(4))
			.andExpect(jsonPath("$.content[0].receivedCommentCount").value(5))
			.andExpect(jsonPath("$.content[0].receivedLikeCount").value(6))
			.andExpect(jsonPath("$.content[0].dailyPowerRank").value(2))

			// 오늘
			.andExpect(jsonPath("$.content[1].activityDate")
				.value(todayInstant.toString()))
			.andExpect(jsonPath("$.content[1].reviewCount").value(7))
			.andExpect(jsonPath("$.content[1].commentCount").value(8))
			.andExpect(jsonPath("$.content[1].likeCount").value(9))
			.andExpect(jsonPath("$.content[1].receivedCommentCount").value(10))
			.andExpect(jsonPath("$.content[1].receivedLikeCount").value(11))
			.andExpect(jsonPath("$.content[1].dailyPowerRank").value(1))

			// 데이터가 없는 날짜는 0과 null
			.andExpect(jsonPath("$.content[2].reviewCount").value(0))
			.andExpect(jsonPath("$.content[2].commentCount").value(0))
			.andExpect(jsonPath("$.content[2].likeCount").value(0))
			.andExpect(jsonPath("$.content[2].receivedCommentCount").value(0))
			.andExpect(jsonPath("$.content[2].receivedLikeCount").value(0))
			.andExpect(jsonPath("$.content[2].dailyPowerRank").isEmpty());
	}

	@Test
	@DisplayName("사용자 활동 통계 조회 - 최초 활동일부터 현재 30일 주기를 반환한다")
	void getUserActivityStats_currentCycle() throws Exception {
		LocalDate today = LocalDate.now(SEOUL_ZONE);

		// 오늘이 최초 활동일부터 35일째가 되도록 설정
		LocalDate firstActivityDate = today.minusDays(35);
		LocalDate currentCycleStart = firstActivityDate.plusDays(30);

		Instant firstActivityInstant =
			firstActivityDate
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		Instant currentCycleActivityInstant =
			currentCycleStart
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_1,
			firstActivityInstant,
			1,
			1,
			1,
			1,
			1,
			10
		));

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_1,
			currentCycleActivityInstant,
			5,
			5,
			5,
			5,
			5,
			3
		));

		mockMvc.perform(get(
				"/api/users/{userId}/activity-stats",
				USER_ID_1
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(30))

			// 과거 1주기가 아니라 현재 2주기의 시작일부터 반환
			.andExpect(jsonPath("$.content[0].activityDate")
				.value(currentCycleActivityInstant.toString()))
			.andExpect(jsonPath("$.content[0].reviewCount").value(5))
			.andExpect(jsonPath("$.content[0].dailyPowerRank").value(3));
	}

	@Test
	@DisplayName("사용자 활동 통계 조회 - 순위가 미확정이면 dailyPowerRank는 null이다")
	void getUserActivityStats_nullDailyPowerRank() throws Exception {
		Instant activityDate =
			LocalDate.now(SEOUL_ZONE)
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_1,
			activityDate,
			1,
			2,
			3,
			4,
			5,
			null
		));

		mockMvc.perform(get(
				"/api/users/{userId}/activity-stats",
				USER_ID_1
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(30))
			.andExpect(jsonPath("$.content[0].activityDate")
				.value(activityDate.toString()))
			.andExpect(jsonPath("$.content[0].reviewCount").value(1))
			.andExpect(jsonPath("$.content[0].dailyPowerRank").isEmpty());
	}

	@Test
	@DisplayName("사용자 활동 통계 조회 - 해당 사용자의 데이터가 없으면 빈 목록을 반환한다")
	void getUserActivityStats_empty() throws Exception {
		mockMvc.perform(get(
				"/api/users/{userId}/activity-stats",
				USER_ID_1
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(USER_ID_1))
			.andExpect(jsonPath("$.content").isEmpty());
	}

	@Test
	@DisplayName("사용자 활동 통계 조회 - 다른 사용자의 통계는 포함하지 않는다")
	void getUserActivityStats_excludesOtherUsers() throws Exception {
		Instant activityDate =
			LocalDate.now(SEOUL_ZONE)
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_1,
			activityDate,
			1,
			1,
			1,
			1,
			1,
			1
		));

		userActivityStatsRepository.save(userActivityStatsDocument(
			USER_ID_2,
			activityDate,
			9,
			9,
			9,
			9,
			9,
			2
		));

		mockMvc.perform(get(
				"/api/users/{userId}/activity-stats",
				USER_ID_1
			))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(USER_ID_1))
			.andExpect(jsonPath("$.content.length()").value(30))
			.andExpect(jsonPath("$.content[0].activityDate")
				.value(activityDate.toString()))
			.andExpect(jsonPath("$.content[0].reviewCount").value(1))
			.andExpect(jsonPath("$.content[0].commentCount").value(1))
			.andExpect(jsonPath("$.content[0].dailyPowerRank").value(1));
	}

	// ---------- 테스트 데이터 생성 ----------

	private PopularBooksDocument popularBooksDocument(
		PeriodType periodType,
		List<PopularBooksDocument.Ranking> rankings
	) {
		PopularBooksDocument document = new PopularBooksDocument();

		ReflectionTestUtils.setField(document, "periodType", periodType);
		ReflectionTestUtils.setField(document, "periodDate", PERIOD_DATE);
		ReflectionTestUtils.setField(document, "rankings", rankings);
		ReflectionTestUtils.setField(document, "createdAt", CREATED_AT);
		ReflectionTestUtils.setField(document, "updatedAt", CREATED_AT);

		return document;
	}

	private PopularBooksDocument.Ranking popularBookRanking(
		int rank,
		String bookId,
		String title,
		String author,
		String thumbnailUrl,
		double score,
		int reviewCount,
		double avgRating
	) {
		PopularBooksDocument.Ranking ranking =
			new PopularBooksDocument.Ranking();

		ReflectionTestUtils.setField(ranking, "rank", rank);
		ReflectionTestUtils.setField(ranking, "bookId", bookId);
		ReflectionTestUtils.setField(ranking, "title", title);
		ReflectionTestUtils.setField(ranking, "author", author);
		ReflectionTestUtils.setField(ranking, "thumbnailUrl", thumbnailUrl);
		ReflectionTestUtils.setField(ranking, "score", score);
		ReflectionTestUtils.setField(ranking, "reviewCount", reviewCount);
		ReflectionTestUtils.setField(ranking, "avgRating", avgRating);

		return ranking;
	}

	private PopularReviewsDocument popularReviewsDocument(
		PeriodType periodType,
		List<PopularReviewsDocument.Ranking> rankings
	) {
		PopularReviewsDocument document = new PopularReviewsDocument();

		ReflectionTestUtils.setField(document, "periodType", periodType);
		ReflectionTestUtils.setField(document, "periodDate", PERIOD_DATE);
		ReflectionTestUtils.setField(document, "rankings", rankings);
		ReflectionTestUtils.setField(document, "createdAt", CREATED_AT);
		ReflectionTestUtils.setField(document, "updatedAt", CREATED_AT);

		return document;
	}

	private PopularReviewsDocument.Ranking popularReviewRanking(
		int rank,
		String reviewId,
		String userId,
		String bookId,
		String nickname,
		String title,
		String thumbnailUrl,
		String content,
		double rating,
		double score,
		int likeCount,
		int commentCount,
		Instant createdAt
	) {
		PopularReviewsDocument.Ranking ranking =
			new PopularReviewsDocument.Ranking();

		ReflectionTestUtils.setField(ranking, "rank", rank);
		ReflectionTestUtils.setField(ranking, "reviewId", reviewId);
		ReflectionTestUtils.setField(ranking, "userId", userId);
		ReflectionTestUtils.setField(ranking, "bookId", bookId);
		ReflectionTestUtils.setField(ranking, "nickname", nickname);
		ReflectionTestUtils.setField(ranking, "title", title);
		ReflectionTestUtils.setField(ranking, "thumbnailUrl", thumbnailUrl);
		ReflectionTestUtils.setField(ranking, "content", content);
		ReflectionTestUtils.setField(ranking, "rating", rating);
		ReflectionTestUtils.setField(ranking, "score", score);
		ReflectionTestUtils.setField(ranking, "likeCount", likeCount);
		ReflectionTestUtils.setField(ranking, "commentCount", commentCount);
		ReflectionTestUtils.setField(ranking, "createdAt", createdAt);

		return ranking;
	}

	private PowerUsersDocument powerUsersDocument(
		PeriodType periodType,
		List<PowerUsersDocument.Ranking> rankings
	) {
		PowerUsersDocument document = new PowerUsersDocument();

		ReflectionTestUtils.setField(document, "periodType", periodType);
		ReflectionTestUtils.setField(document, "periodDate", PERIOD_DATE);
		ReflectionTestUtils.setField(document, "rankings", rankings);
		ReflectionTestUtils.setField(document, "createdAt", CREATED_AT);
		ReflectionTestUtils.setField(document, "updatedAt", CREATED_AT);

		return document;
	}

	private PowerUsersDocument.Ranking powerUserRanking(
		int rank,
		String userId,
		String nickname,
		double activityScore,
		double reviewScore,
		int likeCount,
		int commentCount
	) {
		PowerUsersDocument.Ranking ranking =
			new PowerUsersDocument.Ranking();

		ReflectionTestUtils.setField(ranking, "rank", rank);
		ReflectionTestUtils.setField(ranking, "userId", userId);
		ReflectionTestUtils.setField(ranking, "nickname", nickname);
		ReflectionTestUtils.setField(
			ranking,
			"activityScore",
			activityScore
		);
		ReflectionTestUtils.setField(
			ranking,
			"reviewScore",
			reviewScore
		);
		ReflectionTestUtils.setField(ranking, "likeCount", likeCount);
		ReflectionTestUtils.setField(
			ranking,
			"commentCount",
			commentCount
		);

		return ranking;
	}

	private UserActivityStatsDocument userActivityStatsDocument(
		String userId,
		Instant activityDate,
		int reviewCount,
		int commentCount,
		int likeCount,
		int receivedCommentCount,
		int receivedLikeCount,
		Integer dailyPowerRank
	) {
		UserActivityStatsDocument document =
			new UserActivityStatsDocument();

		ReflectionTestUtils.setField(document, "userId", userId);
		ReflectionTestUtils.setField(
			document,
			"activityDate",
			activityDate
		);
		ReflectionTestUtils.setField(
			document,
			"reviewCount",
			reviewCount
		);
		ReflectionTestUtils.setField(
			document,
			"commentCount",
			commentCount
		);
		ReflectionTestUtils.setField(document, "likeCount", likeCount);
		ReflectionTestUtils.setField(
			document,
			"receivedCommentCount",
			receivedCommentCount
		);
		ReflectionTestUtils.setField(
			document,
			"receivedLikeCount",
			receivedLikeCount
		);
		ReflectionTestUtils.setField(
			document,
			"dailyPowerRank",
			dailyPowerRank
		);
		ReflectionTestUtils.setField(document, "createdAt", CREATED_AT);
		ReflectionTestUtils.setField(document, "updatedAt", CREATED_AT);

		return document;
	}
}