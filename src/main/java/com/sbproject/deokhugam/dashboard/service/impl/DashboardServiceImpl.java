package com.sbproject.deokhugam.dashboard.service.impl;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.document.UserActivityStatsDocument;
import com.sbproject.deokhugam.dashboard.dto.PopularBooksRankingResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsRankingResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersRankingResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;
import com.sbproject.deokhugam.dashboard.dto.UserActivityStatsResponse;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import com.sbproject.deokhugam.dashboard.repository.PopularBooksRepository;
import com.sbproject.deokhugam.dashboard.repository.PopularReviewsRepository;
import com.sbproject.deokhugam.dashboard.repository.PowerUsersRepository;
import com.sbproject.deokhugam.dashboard.repository.UserActivityStatsRepository;
import com.sbproject.deokhugam.dashboard.service.DashboardService;
import com.sbproject.deokhugam.storage.FileStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final PopularBooksRepository popularBooksRepository;
  private final PopularReviewsRepository popularReviewsRepository;
  private final PowerUsersRepository powerUsersRepository;
  private final UserActivityStatsRepository userActivityStatsRepository;
  private final FileStorage fileStorage;
	private static final ZoneId SEOUL_ZONE =
		ZoneId.of("Asia/Seoul");

	private static final int ACTIVITY_CYCLE_DAYS = 30;

  @Override
  public PopularBooksResponse getPopularBooks(PeriodType  period, String direction, int limit) {

	  PopularBooksDocument doc = popularBooksRepository
		  .findTopByPeriodTypeOrderByPeriodDateDesc(period)
		  .orElse(null);

	  if (doc == null) {
		  return PopularBooksResponse.empty(period);
	  }

    List<PopularBooksRankingResponse> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingInt(PopularBooksDocument.Ranking::getRank).reversed()
            : Comparator.comparingInt(PopularBooksDocument.Ranking::getRank))
        .limit(limit)
		.map(ranking -> PopularBooksRankingResponse.from(ranking, doc, fileStorage.getUrl(ranking.getThumbnailUrl())))
        .toList();

    return new PopularBooksResponse(doc, rankings);
  }

  @Override
  public PopularReviewsResponse getPopularReviews(PeriodType  period, String direction, int limit) {

	  PopularReviewsDocument doc = popularReviewsRepository
		  .findTopByPeriodTypeOrderByPeriodDateDesc(period)
		  .orElse(null);

	  if (doc == null) {
		  return PopularReviewsResponse.empty(period);
	  }

    List<PopularReviewsRankingResponse> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingDouble(PopularReviewsDocument.Ranking::getScore).reversed()
            : Comparator.comparingDouble(PopularReviewsDocument.Ranking::getScore))
        .limit(limit)
		.map(ranking ->
			PopularReviewsRankingResponse.from(ranking, doc, fileStorage.getUrl(ranking.getThumbnailUrl()))
		)
        .toList();

    return new PopularReviewsResponse(doc, rankings);
  }

  @Override
  public PowerUsersResponse getPowerUsers(PeriodType  period, String direction, int limit) {

	  PowerUsersDocument doc = powerUsersRepository
		  .findTopByPeriodTypeOrderByPeriodDateDesc(period)
		  .orElse(null);

	  if (doc == null) {
		  return PowerUsersResponse.empty(period);
	  }

	  List<PowerUsersRankingResponse> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingInt(PowerUsersDocument.Ranking::getRank).reversed()
            : Comparator.comparingInt(PowerUsersDocument.Ranking::getRank))
        .limit(limit)
		  .map(ranking -> PowerUsersRankingResponse.from(ranking, doc))
        .toList();

    return new PowerUsersResponse(doc, rankings);
  }

	@Override
	public UserActivityStatsResponse getUserActivityStats(
		String userId
	) {
		LocalDate today = LocalDate.now(SEOUL_ZONE);

		// GitHub 잔디처럼 "오늘부터 최근 30일" 고정 윈도우
		LocalDate rangeStart = today.minusDays(ACTIVITY_CYCLE_DAYS - 1);

		Instant startInstant =
			rangeStart
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		Instant endInstant =
			today.plusDays(1)
				.atStartOfDay(SEOUL_ZONE)
				.toInstant();

		List<UserActivityStatsDocument> docs =
			userActivityStatsRepository
				.findActivityStatsByPeriod(
					userId,
					startInstant,
					endInstant
				);

		/*
		 * 활동 기록이 한 건도 없는 사용자는 빈 응답을 반환한다.
		 */
		if (docs.isEmpty()) {
			return UserActivityStatsResponse.empty(userId);
		}

		Map<LocalDate, UserActivityStatsDocument> documentByDate =
			docs.stream()
				.collect(Collectors.toMap(
					document -> document
						.getActivityDate()
						.atZone(SEOUL_ZONE)
						.toLocalDate(),
					Function.identity()
				));

		List<UserActivityStatsResponse.UserActivityStatEntry> content =
			IntStream.range(0, ACTIVITY_CYCLE_DAYS)
				.mapToObj(rangeStart::plusDays)
				.map(date -> {
					UserActivityStatsDocument document =
						documentByDate.get(date);

					if (document != null) {
						return UserActivityStatsResponse
							.UserActivityStatEntry
							.from(document);
					}

					return UserActivityStatsResponse
						.UserActivityStatEntry
						.empty(
							date.atStartOfDay(SEOUL_ZONE)
								.toInstant()
						);
				})
				.toList();

		return new UserActivityStatsResponse(
			userId,
			content
		);
	}

}