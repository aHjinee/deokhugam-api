package com.sbproject.deokhugam.dashboard.dto;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class PopularBooksResponse {

  private final PeriodType periodType;
  private final Instant periodDate;
  private final List<PopularBooksRankingResponse> content;

  public PopularBooksResponse(PopularBooksDocument doc, List<PopularBooksRankingResponse> rankings) {
    this.periodType = doc.getPeriodType();
    this.periodDate = doc.getPeriodDate();
    this.content = rankings;
  }
}