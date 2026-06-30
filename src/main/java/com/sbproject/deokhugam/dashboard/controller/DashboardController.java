package com.sbproject.deokhugam.dashboard.controller;

import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;
import com.sbproject.deokhugam.dashboard.dto.UserActivityStatsResponse;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import com.sbproject.deokhugam.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/books/popular")
  public ResponseEntity<PopularBooksResponse> getPopularBooks(
      @RequestParam(defaultValue = "ALL_TIME") PeriodType period,
      @RequestParam(defaultValue = "ASC") String direction,
      @RequestParam(defaultValue = "4")
	  @Min(1) @Max(4) int limit) {
    return ResponseEntity.ok(dashboardService.getPopularBooks(period, direction, limit));
  }

  @GetMapping("/reviews/popular")
  public ResponseEntity<PopularReviewsResponse> getPopularReviews(
      @RequestParam(defaultValue = "ALL_TIME") PeriodType period,
      @RequestParam(defaultValue = "DESC") String direction,
      @RequestParam(defaultValue = "20")
	  @Min(1) @Max(20) int limit) {
    return ResponseEntity.ok(dashboardService.getPopularReviews(period, direction, limit));
  }

  @GetMapping("/users/power")
  public ResponseEntity<PowerUsersResponse> getPowerUsers(
      @RequestParam(defaultValue = "ASC") String direction,
      @RequestParam(defaultValue = "10")
	  @Min(1) @Max(10) int limit){
    return ResponseEntity.ok(dashboardService.getPowerUsers(PeriodType.ALL_TIME, direction, limit));
  }

  @GetMapping("/users/{userId}/activity-stats")
  public ResponseEntity<UserActivityStatsResponse> getUserActivityStats(
	  @PathVariable String userId) {
	  return ResponseEntity.ok(dashboardService.getUserActivityStats(userId));
	}
}