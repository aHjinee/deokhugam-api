package com.sbproject.deokhugam.book.dto;

import java.util.List;

public record NaverSearchBookResponse(
	String lastBuildDate,
	Integer total,
	Integer start,
	Integer display,
	List<NaverBookItem> items
) {
}
