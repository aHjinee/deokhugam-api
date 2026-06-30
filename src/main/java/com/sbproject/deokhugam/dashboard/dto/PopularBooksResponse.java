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

  private PopularBooksResponse(
	  PeriodType periodType,
	  Instant periodDate,
	  List<PopularBooksRankingResponse> content
  ) {
	  this.periodType = periodType;
	  this.periodDate = periodDate;
	  this.content = content;
  }

  public static PopularBooksResponse empty(PeriodType periodType) {
	  return new PopularBooksResponse(
		  periodType,
		  null,
		  List.of()
		);
	}
}