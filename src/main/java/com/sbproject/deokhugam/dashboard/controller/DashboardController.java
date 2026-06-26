package com.sbproject.deokhugam.dashboard.controller;

import com.sbproject.deokhugam.dashboard.dto.PopularBooksResponse;
import com.sbproject.deokhugam.dashboard.dto.PopularReviewsResponse;
import com.sbproject.deokhugam.dashboard.dto.PowerUsersResponse;
import com.sbproject.deokhugam.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/books/popular")
  public ResponseEntity<PopularBooksResponse> getPopularBooks(
      @RequestParam(defaultValue = "ALL_TIME") String period,
      @RequestParam(defaultValue = "ASC") String direction,
      @RequestParam(defaultValue = "4") int limit) {
    return ResponseEntity.ok(dashboardService.getPopularBooks(period, direction, limit));
  }

  @GetMapping("/reviews/popular")
  public ResponseEntity<PopularReviewsResponse> getPopularReviews(
      @RequestParam(defaultValue = "ALL_TIME") String period,
      @RequestParam(defaultValue = "DESC") String direction,
      @RequestParam(defaultValue = "20") int limit) {
    return ResponseEntity.ok(dashboardService.getPopularReviews(period, direction, limit));
  }

  @GetMapping("/users/power")
  public ResponseEntity<PowerUsersResponse> getPowerUsers(
      @RequestParam(defaultValue = "ASC") String direction,
      @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(dashboardService.getPowerUsers(direction, limit));
  }
}