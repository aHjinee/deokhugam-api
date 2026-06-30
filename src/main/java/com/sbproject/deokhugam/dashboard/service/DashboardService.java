package com.sbproject.deokhugam.dashboard.service;

import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;
import com.sbproject.deokhugam.dashboard.dto.UserActivityStatsResponse;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;

public interface DashboardService {

  PopularBooksResponse getPopularBooks(PeriodType period, String direction, int limit);

  PopularReviewsResponse getPopularReviews(PeriodType period, String direction, int limit);

  PowerUsersResponse getPowerUsers(PeriodType  period, String direction, int limit);

  UserActivityStatsResponse getUserActivityStats(String userId);
}