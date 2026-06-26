package com.sbproject.deokhugam.dashboard.dto;

import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class PopularReviewsResponse {

  private final PeriodType periodType;
  private final Instant periodDate;
  private final List<PopularReviewsDocument.Ranking> content;

  public PopularReviewsResponse(PopularReviewsDocument doc, List<PopularReviewsDocument.Ranking> rankings) {
    this.periodType = doc.getPeriodType();
    this.periodDate = doc.getPeriodDate();
    this.content = rankings;
  }
}