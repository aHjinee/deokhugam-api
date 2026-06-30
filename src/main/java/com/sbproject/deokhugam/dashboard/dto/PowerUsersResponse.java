package com.sbproject.deokhugam.dashboard.dto;

import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class PowerUsersResponse {

  private final PeriodType periodType;
  private final Instant periodDate;
	private final List<PowerUsersRankingResponse> content;

  public PowerUsersResponse(PowerUsersDocument doc, List<PowerUsersRankingResponse> rankings) {
    this.periodType = doc.getPeriodType();
    this.periodDate = doc.getPeriodDate();
    this.content = rankings;
  }
  private PowerUsersResponse(
	  PeriodType periodType,
	  Instant periodDate,
	  List<PowerUsersRankingResponse> content
  ) {
	  this.periodType = periodType;
	  this.periodDate = periodDate;
	  this.content = content;
  }

  public static PowerUsersResponse empty(PeriodType periodType) {
	  return new PowerUsersResponse(
		  periodType,
		  null,
		  List.of()
	  );
  }
}