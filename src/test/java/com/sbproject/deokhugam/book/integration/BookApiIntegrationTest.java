package com.sbproject.deokhugam.book.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbproject.deokhugam.book.client.GeminiOcrClient;
import com.sbproject.deokhugam.book.client.NaverClient;
import com.sbproject.deokhugam.book.dto.NaverBookDto;
import com.sbproject.deokhugam.storage.FileStorage;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookApiIntegrationTest {

	// data.sql 시드 도서 (삭제되지 않음)
	private static final String SEED_BOOK_ID = "0194263e-3a80-7972-8208-ba3c6c031199";
	private static final String SEED_ISBN = "9788936434120";
	private static final String NEW_ISBN = "9780134685991";

	// 1x1 PNG (Tika가 시그니처로 image/png 판별 → ImageFileValidator 통과용)
	private static final byte[] PNG_BYTES = Base64.getDecoder().decode(
		"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	NaverClient naverClient;
	@MockitoBean
	GeminiOcrClient ocrClient;
	@MockitoBean
	FileStorage fileStorage;

	private Map<String, Object> bookBody(String title, String isbn) {
		return Map.of(
			"title", title,
			"author", "테스트 저자",
			"description", "통합 테스트용 설명",
			"publisher", "테스트 출판사",
			"publishedDate", "2020-01-01",
			"isbn", isbn
		);
	}

	private MockMultipartFile jsonPart(Object body) throws Exception {
		return new MockMultipartFile(
			"bookData", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(body));
	}

	// ---------- 목록 조회 GET /api/books ----------

	@Test
	@DisplayName("도서 목록 조회 - 시드된 도서가 반환된다")
	void getBookList_success() throws Exception {
		mockMvc.perform(get("/api/books"))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.content").isArray())
		       .andExpect(jsonPath("$.totalElements").value(36));
	}

	@Test
	@DisplayName("도서 목록 조회 - limit이 1 미만이면 400 (@Validated + @Min)")
	void getBookList_invalidLimit() throws Exception {
		mockMvc.perform(get("/api/books").param("limit", "0"))
		       .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("도서 목록 조회 - limit보다 많으면 hasNext와 nextCursor를 반환 (커서 페이지네이션)")
	void getBookList_pagination() throws Exception {
		mockMvc.perform(get("/api/books")
				       .param("orderBy", "title")
				       .param("limit", "2"))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.content.length()").value(2))
		       .andExpect(jsonPath("$.hasNext").value(true))
		       .andExpect(jsonPath("$.nextCursor").isNotEmpty());
	}

	// ---------- 단건 조회 GET /api/books/{id} ----------

	@Test
	@DisplayName("도서 단건 조회 - 성공")
	void getBook_success() throws Exception {
		mockMvc.perform(get("/api/books/{id}", SEED_BOOK_ID))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.isbn").value(SEED_ISBN))
		       .andExpect(jsonPath("$.title").value("채식주의자"));
	}

	@Test
	@DisplayName("도서 단건 조회 - 없는 ID면 404")
	void getBook_notFound() throws Exception {
		mockMvc.perform(get("/api/books/{id}", UUID.randomUUID()))
		       .andExpect(status().isNotFound());
	}

	// ---------- 생성 POST /api/books ----------

	@Test
	@DisplayName("도서 생성 - 유효한 요청이면 201")
	void createBook_success() throws Exception {
		mockMvc.perform(multipart("/api/books").file(jsonPart(bookBody("테스트 도서", NEW_ISBN))))
		       .andExpect(status().isCreated())
		       .andExpect(jsonPath("$.isbn").value(NEW_ISBN))
		       .andExpect(jsonPath("$.title").value("테스트 도서"));
	}

	@Test
	@DisplayName("도서 생성 - 썸네일이 있으면 저장 후 URL을 사용")
	void createBook_withThumbnail() throws Exception {
		given(fileStorage.save(any())).willReturn("storageKey");
		given(fileStorage.getUrl("storageKey")).willReturn("http://localhost/files/storageKey");
		MockMultipartFile thumbnail = new MockMultipartFile(
			"thumbnailImage", "cover.png", MediaType.IMAGE_PNG_VALUE, PNG_BYTES);

		mockMvc.perform(multipart("/api/books")
				       .file(jsonPart(bookBody("썸네일 도서", NEW_ISBN)))
				       .file(thumbnail))
		       .andExpect(status().isCreated())
		       .andExpect(jsonPath("$.thumbnailUrl").value("http://localhost/files/storageKey"));

		then(fileStorage).should().save(any());
	}

	@Test
	@DisplayName("도서 생성 - title이 비면 400 (@NotBlank)")
	void createBook_blankTitle() throws Exception {
		mockMvc.perform(multipart("/api/books").file(jsonPart(bookBody("", NEW_ISBN))))
		       .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("도서 생성 - 이미 존재하는 ISBN이면 409")
	void createBook_duplicateIsbn() throws Exception {
		mockMvc.perform(multipart("/api/books").file(jsonPart(bookBody("중복 도서", SEED_ISBN))))
		       .andExpect(status().isConflict());
	}

	// ---------- 수정 PATCH /api/books/{id} ----------

	@Test
	@DisplayName("도서 수정 - 메타데이터 수정 성공")
	void updateBook_success() throws Exception {
		Map<String, Object> body = Map.of(
			"title", "수정된 제목",
			"author", "수정된 저자",
			"description", "수정된 설명",
			"publisher", "수정된 출판사",
			"publishedDate", "2021-05-05"
		);
		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{id}", SEED_BOOK_ID).file(jsonPart(body)))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.title").value("수정된 제목"));
	}

	@Test
	@DisplayName("도서 수정 - 썸네일이 있으면 저장 후 URL로 교체")
	void updateBook_withThumbnail() throws Exception {
		given(fileStorage.save(any())).willReturn("storageKey");
		given(fileStorage.getUrl("storageKey")).willReturn("http://localhost/files/new");
		Map<String, Object> body = Map.of(
			"title", "썸네일 교체",
			"author", "저자",
			"description", "설명",
			"publisher", "출판사",
			"publishedDate", "2021-05-05"
		);
		MockMultipartFile thumbnail = new MockMultipartFile(
			"thumbnailImage", "cover.png", MediaType.IMAGE_PNG_VALUE, PNG_BYTES);

		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{id}", SEED_BOOK_ID)
				       .file(jsonPart(body))
				       .file(thumbnail))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.thumbnailUrl").value("http://localhost/files/new"));

		then(fileStorage).should().save(any());
	}

	@Test
	@DisplayName("도서 수정 - title이 비면 400")
	void updateBook_blankTitle() throws Exception {
		Map<String, Object> body = Map.of(
			"title", "",
			"author", "저자",
			"description", "설명",
			"publisher", "출판사",
			"publishedDate", "2021-05-05"
		);
		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{id}", SEED_BOOK_ID).file(jsonPart(body)))
		       .andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("도서 수정 - 없는 ID면 404")
	void updateBook_notFound() throws Exception {
		Map<String, Object> body = Map.of(
			"title", "제목",
			"author", "저자",
			"description", "설명",
			"publisher", "출판사",
			"publishedDate", "2021-05-05"
		);
		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{id}", UUID.randomUUID()).file(jsonPart(body)))
		       .andExpect(status().isNotFound());
	}

	// ---------- 논리 삭제 DELETE /api/books/{id} ----------

	@Test
	@DisplayName("도서 논리 삭제 - 성공하면 204, 이후 조회 시 404")
	void deleteBook_success() throws Exception {
		mockMvc.perform(delete("/api/books/{id}", SEED_BOOK_ID))
		       .andExpect(status().isNoContent());

		mockMvc.perform(get("/api/books/{id}", SEED_BOOK_ID))
		       .andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("도서 논리 삭제 - 없는 ID면 404")
	void deleteBook_notFound() throws Exception {
		mockMvc.perform(delete("/api/books/{id}", UUID.randomUUID()))
		       .andExpect(status().isNotFound());
	}

	// ---------- 물리 삭제 DELETE /api/books/{id}/hard ----------

	@Test
	@DisplayName("도서 물리 삭제 - 성공하면 204")
	void hardDeleteBook_success() throws Exception {
		mockMvc.perform(delete("/api/books/{id}/hard", SEED_BOOK_ID))
		       .andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("도서 물리 삭제 - 없는 ID면 404")
	void hardDeleteBook_notFound() throws Exception {
		mockMvc.perform(delete("/api/books/{id}/hard", UUID.randomUUID()))
		       .andExpect(status().isNotFound());
	}

	// ---------- ISBN OCR POST /api/books/isbn/ocr ----------

	@Test
	@DisplayName("ISBN OCR - 실제 이미지로 검증 통과 후 추출 결과 반환")
	void extractIsbn_success() throws Exception {
		given(ocrClient.extractIsbn(any())).willReturn(NEW_ISBN);
		MockMultipartFile image = new MockMultipartFile(
			"image", "book.png", MediaType.IMAGE_PNG_VALUE, PNG_BYTES);

		mockMvc.perform(multipart("/api/books/isbn/ocr").file(image))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$").value(NEW_ISBN));
	}

	@Test
	@DisplayName("ISBN OCR - 헤더는 이미지지만 실제 바이트가 이미지가 아니면 400 (Tika 검증)")
	void extractIsbn_invalidFile() throws Exception {
		MockMultipartFile notImage = new MockMultipartFile(
			"image", "fake.png", MediaType.IMAGE_PNG_VALUE, "this is not an image".getBytes());

		mockMvc.perform(multipart("/api/books/isbn/ocr").file(notImage))
		       .andExpect(status().isBadRequest());
	}

	// ---------- 네이버 도서 정보 GET /api/books/info ----------

	@Test
	@DisplayName("네이버 도서 정보 조회 - NaverClient 결과를 그대로 반환")
	void getBookInfo_success() throws Exception {
		NaverBookDto dto = new NaverBookDto(
			"채식주의자", "한강", "설명", "창비", LocalDate.parse("2007-10-30"), SEED_ISBN, "base64image");
		given(naverClient.getBookInfo(SEED_ISBN)).willReturn(dto);

		mockMvc.perform(get("/api/books/info").param("isbn", SEED_ISBN))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.isbn").value(SEED_ISBN))
		       .andExpect(jsonPath("$.title").value("채식주의자"));
	}
}
