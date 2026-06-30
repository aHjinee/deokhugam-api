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
	private final List<PopularReviewsRankingResponse> content;

  public PopularReviewsResponse(PopularReviewsDocument doc, List<PopularReviewsRankingResponse> rankings) {
    this.periodType = doc.getPeriodType();
    this.periodDate = doc.getPeriodDate();
    this.content = rankings;
  }
  private PopularReviewsResponse(
	  PeriodType periodType,
	  Instant periodDate,
	  List<PopularReviewsRankingResponse> content
  ) {
	  this.periodType = periodType;
	  this.periodDate = periodDate;
	  this.content = content;
  }

  public static PopularReviewsResponse empty(PeriodType periodType) {
	  return new PopularReviewsResponse(
		  periodType,
		  null,
		  List.of()
	  );
  }

}