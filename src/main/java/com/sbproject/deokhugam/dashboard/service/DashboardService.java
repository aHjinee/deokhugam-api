package com.sbproject.deokhugam.dashboard.service;

import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;

public interface DashboardService {

  PopularBooksResponse getPopularBooks(String period, String direction, int limit);

  PopularReviewsResponse getPopularReviews(String period, String direction, int limit);

  PowerUsersResponse getPowerUsers(String direction, int limit);
}