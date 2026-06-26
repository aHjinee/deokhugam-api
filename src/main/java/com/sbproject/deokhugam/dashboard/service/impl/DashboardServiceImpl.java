package com.sbproject.deokhugam.dashboard.service.impl;

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

  @Override
  public PopularBooksResponse getPopularBooks(String period, String direction, int limit) {
    PopularBooksDocument doc = popularBooksRepository
        .findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.valueOf(period))
        .orElseThrow(() -> new RuntimeException("인기 도서 데이터가 없습니다."));

    List<PopularBooksDocument.Ranking> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingInt(PopularBooksDocument.Ranking::getRank).reversed()
            : Comparator.comparingInt(PopularBooksDocument.Ranking::getRank))
        .limit(limit)
        .toList();

    return new PopularBooksResponse(doc, rankings);
  }

  @Override
  public PopularReviewsResponse getPopularReviews(String period, String direction, int limit) {
    PopularReviewsDocument doc = popularReviewsRepository
        .findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.valueOf(period))
        .orElseThrow(() -> new RuntimeException("인기 리뷰 데이터가 없습니다."));

    List<PopularReviewsDocument.Ranking> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingDouble(PopularReviewsDocument.Ranking::getScore).reversed()
            : Comparator.comparingDouble(PopularReviewsDocument.Ranking::getScore))
        .limit(limit)
        .toList();

    return new PopularReviewsResponse(doc, rankings);
  }

  @Override
  public PowerUsersResponse getPowerUsers(String direction, int limit) {
    PowerUsersDocument doc = powerUsersRepository
        .findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType.valueOf("ALL_TIME"))
        .orElseThrow(() -> new RuntimeException("파워 유저 데이터가 없습니다."));

    List<PowerUsersDocument.Ranking> rankings = doc.getRankings().stream()
        .sorted("DESC".equalsIgnoreCase(direction)
            ? Comparator.comparingInt(PowerUsersDocument.Ranking::getRank).reversed()
            : Comparator.comparingInt(PowerUsersDocument.Ranking::getRank))
        .limit(limit)
        .toList();

    return new PowerUsersResponse(doc, rankings);
  }
}