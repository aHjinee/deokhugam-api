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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final PopularBooksRepository popularBooksRepository;
  private final PopularReviewsRepository popularReviewsRepository;
  private final PowerUsersRepository powerUsersRepository;
  private final UserActivityStatsRepository userActivityStatsRepository;

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
		.map(ranking -> PopularBooksRankingResponse.from(ranking, doc))
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
			PopularReviewsRankingResponse.from(ranking, doc)
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
	public UserActivityStatsResponse getUserActivityStats(String userId) {
		List<UserActivityStatsDocument> docs =
			userActivityStatsRepository.findTop30ByUserIdOrderByActivityDateDesc(userId);

		if (docs.isEmpty()) {
			return UserActivityStatsResponse.empty(userId);
		}

		List<UserActivityStatsResponse.UserActivityStatEntry> content = docs.stream()
			.map(UserActivityStatsResponse.UserActivityStatEntry::from)
			.toList();

		return new UserActivityStatsResponse(userId, content);
	}

}