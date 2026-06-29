package com.sbproject.deokhugam.book.client;

import java.net.URI;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.sbproject.deokhugam.book.dto.NaverBookDto;
import com.sbproject.deokhugam.book.dto.NaverBookItem;
import com.sbproject.deokhugam.book.dto.NaverSearchBookResponse;
import com.sbproject.deokhugam.book.exception.NaverBookNotFoundException;

@Component
public class NaverClient {

	private final RestClient restClient;
	private final RestClient imageClient;

	public NaverClient(
		@Value("${NAVER_CLIENT_ID}") String clientId,
		@Value("${NAVER_CLIENT_SECRET}") String clientSecret
	) {
		this.restClient = RestClient.builder()
		                            .baseUrl("https://openapi.naver.com/v1/search")
		                            .defaultHeader("X-Naver-Client-Id", clientId)
		                            .defaultHeader("X-Naver-Client-Secret", clientSecret)
		                            .build();

		this.imageClient = RestClient.create();
	}

	public NaverBookDto getBookInfo(String isbn) {
		NaverSearchBookResponse response = restClient.get()
		                                             .uri("/book_adv?d_isbn={isbn}", isbn)
		                                             .retrieve()
		                                             .body(NaverSearchBookResponse.class);
		if (response == null || response.items().isEmpty()) {
			throw NaverBookNotFoundException.withIsbn(isbn);
		}

		NaverBookItem item = response.items().get(0);
		byte[] imageBytes = imageClient.get()
		                               .uri(URI.create(item.image()))
		                               .retrieve()
		                               .body(byte[].class);
		String encoded = Base64.getEncoder().encodeToString(imageBytes);
		return new NaverBookDto(item, encoded);
	}
}
