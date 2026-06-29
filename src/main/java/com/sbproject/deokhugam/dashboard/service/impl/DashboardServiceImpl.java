package com.sbproject.deokhugam.dashboard.service.impl;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.dto.PopularBooksRankingResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsRankingResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersRankingResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import com.sbproject.deokhugam.dashboard.repository.PopularBooksRepository;
import com.sbproject.deokhugam.dashboard.repository.PopularReviewsRepository;
import com.sbproject.deokhugam.dashboard.repository.PowerUsersRepository;
import com.sbproject.deokhugam.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Locale;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final PopularBooksRepository popularBooksRepository;
  private final PopularReviewsRepository popularReviewsRepository;
  private final PowerUsersRepository powerUsersRepository;

  @Override
  public PopularBooksResponse getPopularBooks(String period, String direction, int limit) {
	  PeriodType periodType = parsePeriod(period);

	  PopularBooksDocument doc = popularBooksRepository
		  .findTopByPeriodTypeOrderByPeriodDateDesc(periodType)
		  .orElseThrow(() -> new RuntimeException("인기 도서 데이터가 없습니다."));

    List<PopularBooksRankingResponse> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingInt(PopularBooksDocument.Ranking::getRank).reversed()
            : Comparator.comparingInt(PopularBooksDocument.Ranking::getRank))
        .limit(limit)
		.map(PopularBooksRankingResponse::from)
        .toList();

    return new PopularBooksResponse(doc, rankings);
  }

  @Override
  public PopularReviewsResponse getPopularReviews(String period, String direction, int limit) {
	  PeriodType periodType = parsePeriod(period);

	  PopularReviewsDocument doc = popularReviewsRepository
		  .findTopByPeriodTypeOrderByPeriodDateDesc(periodType)
		  .orElseThrow(() -> new RuntimeException("인기 리뷰 데이터가 없습니다."));

    List<PopularReviewsRankingResponse> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingDouble(PopularReviewsDocument.Ranking::getScore).reversed()
            : Comparator.comparingDouble(PopularReviewsDocument.Ranking::getScore))
        .limit(limit)
		.map(PopularReviewsRankingResponse::from)
        .toList();

    return new PopularReviewsResponse(doc, rankings);
  }

  @Override
  public PowerUsersResponse getPowerUsers(String period, String direction, int limit) {
	  PeriodType periodType = parsePeriod(period);

	  PowerUsersDocument doc = powerUsersRepository
		  .findTopByPeriodTypeOrderByPeriodDateDesc(periodType)
		  .orElseThrow(() -> new RuntimeException("파워 유저 데이터가 없습니다."));


	  List<PowerUsersRankingResponse> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingInt(PowerUsersDocument.Ranking::getRank).reversed()
            : Comparator.comparingInt(PowerUsersDocument.Ranking::getRank))
        .limit(limit)
		  .map(PowerUsersRankingResponse::from)
        .toList();

    return new PowerUsersResponse(doc, rankings);
  }

	private PeriodType parsePeriod(String period) {
		if (period == null || period.isBlank()) {
			return PeriodType.ALL_TIME;
		}

		try {
			return PeriodType.valueOf(
				period.trim().toUpperCase(Locale.ROOT)
			);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
				"지원하지 않는 기간입니다: " + period
			);
		}
	}
}