package com.sbproject.deokhugam.book.client;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.Type;
import com.sbproject.deokhugam.book.dto.IsbnResponseDto;
import com.sbproject.deokhugam.book.exception.IsbnExtractionFailedException;
import com.sbproject.deokhugam.book.exception.OcrProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiOcrClient {
	private static final String SYSTEM_INSTRUCTION = """
		<role>
		너는 도서 이미지(표지, 뒷면, 판권면, 바코드)에서 ISBN을 판독하고 추출하는 '데이터 추출 자동화 시스템'이다.
		</role>
		
		<core_directive>
		1. 오직 이미지에 실제로 '명확하게 보이는' 텍스트와 숫자에만 근거하여 판단하라.
		2. 빛 반사, 훼손, 잘림 등으로 인해 숫자의 일부가 보이지 않는다면 절대 임의로 추측하거나 지어내지(Hallucination) 말고 즉시 실패로 처리하라.
		3. 출력은 반드시 지정된 JSON 스키마를 엄격히 준수하여 순수 JSON 포맷으로만 반환하라.
		</core_directive>
		""";
	private static final String USER_PROMPT = """
		<task>
		제공된 이미지에서 도서의 ISBN을 추출하고, 분석 과정과 결과를 JSON 형식으로 반환하라.
		</task>
		
		<extraction_rules>
		1. 판독 대상: 'ISBN' 텍스트 근처의 번호 또는 바코드 하단의 '사람이 읽을 수 있는 숫자'를 찾는다. (바코드 막대 패턴 자체를 디코딩하지 마라)
		2. 우선순위:
		   - 1순위: '978' 또는 '979'로 시작하는 ISBN-13
		   - 2순위: ISBN-10 (13자리가 없는 경우에만 사용)
		   - [세트 vs 낱권]: 세트/시리즈 ISBN과 낱권(단독 권) ISBN이 혼재할 경우, 반드시 **낱권 ISBN**을 선택한다.
		   - [버전 혼재]: 동일 도서에 10자리와 13자리가 모두 표기되어 있다면 반드시 **ISBN-13**을 선택한다.
		3. 제외 대상: ISBN 13자리 뒤에 따라오는 괄호 안의 부가기호(예: 03810)나 별도의 가격 바코드 숫자는 절대 포함하지 않는다.
		4. 포맷팅: 하이픈(-)과 공백을 모두 제거한 연속된 문자열로 반환한다. ISBN-10의 마지막 한 자리는 숫자 또는 대문자 'X'일 수 있으므로 'X'는 제거하지 말고 그대로 유지한다.
		</extraction_rules>
		
		<validation_rules>
		최종 추출된 값의 길이는 반드시 정확히 10자리 또는 13자리여야 한다.
		ISBN-13은 전부 숫자이고, ISBN-10은 앞 9자리가 숫자, 마지막 1자리는 숫자 또는 'X'이다.
		이 길이를 만족하지 않으면 실패(INVALID_LENGTH)로 처리한다. (단, 수학적 체크섬 계산은 수행하지 않는다)
		</validation_rules>
		
		<output_fields>
		- analysis: 이미지 분석 과정 (바코드 개수, 훼손 여부, 세트/낱권 판단 기준 등). 2문장 이내로 간결하게.
		- found: ISBN 추출 성공 여부
		- isbn: 추출된 10자리/13자리 값 (실패 시 null)
		- reason: 실패 사유 코드 (성공 시 null)
		</output_fields>
		
		<error_codes>
		실패(found=false) 시 reason 필드에 아래 영문 코드 중 하나만 사용하라.
		- NOT_BOOK_IMAGE : 도서 관련 이미지가 아님
		- NO_ISBN : 책은 맞으나 ISBN 텍스트·바코드가 없음
		- BLURRY_OR_DAMAGED : 흐림·빛 반사·잘림 등으로 숫자 판독 불가
		- BARCODE_ONLY_NO_NUMBER : 바코드는 있으나 하단 숫자가 식별 불가
		- MULTIPLE_ISBN_AMBIGUOUS : ISBN이 여럿이고 낱권을 특정할 수 없음
		- INVALID_LENGTH : 숫자를 찾았으나 10자리 또는 13자리가 아님
		- UNKNOWN : 그 밖의 판독 불가 사유
		</error_codes>
		
		<examples>
		[예시 1 - 정상 추출]
		{"analysis": "이미지 하단에 2개의 바코드가 존재함. 왼쪽 바코드 하단에 '978'로 시작하는 13자리 숫자가 명확히 보이며, 오른쪽은 부가기호로 판단됨. 훼손 없이 선명하므로 추출 성공.", "found": true, "isbn": "9781234567890", "reason": null}
		
		[예시 2 - 세트와 낱권 혼재 (낱권 선택)]
		{"analysis": "판권면에 'ISBN 979...001 (세트)'와 'ISBN 979...015'가 표기됨. 낱권 ISBN을 우선해야 하므로 세트 번호를 제외하고 단권 번호를 선택함.", "found": true, "isbn": "9791112223344", "reason": null}
		
		[예시 3 - 빛 반사로 인한 판독 불가 (실패)]
		{"analysis": "책 뒷면 바코드 영역이 확인되나, 중앙의 빛 반사로 인해 하단 숫자의 3자리가 날아가 식별이 불가능함. 임의 추측을 금지하는 규칙에 따라 실패 처리함.", "found": false, "isbn": null, "reason": "BLURRY_OR_DAMAGED"}
		</examples>
		""";
	private static final String MODEL = "gemini-3.1-flash-lite";

	private final ObjectMapper objectMapper;
	private final Client geminiClient;

	public String extractIsbn(MultipartFile image) {
		long start = System.nanoTime();
		GenerateContentResponse response = geminiClient.models
			.generateContent(MODEL, buildContent(image), buildConfig());
		long elapsed = (System.nanoTime() - start) / 1_000_000;
		log.info("Gemini OCR 응답 완료 - 소요 시간: {}ms", elapsed);
		return parseIsbn(response);
	}


	private GenerateContentConfig buildConfig() {
		Schema schema = Schema.builder()
		                      .type(Type.Known.OBJECT)
		                      .properties(ImmutableMap.of(
								  "analysis",
			                      com.google.genai.types.Schema.builder()
			                                                   .type(Type.Known.STRING)
			                                                   .description("짧은 분석 과정")
			                                                   .build(),
			                      "found", com.google.genai.types.Schema.builder().type(Type.Known.BOOLEAN).build(),
			                      "isbn", com.google.genai.types.Schema.builder()
			                                                           .type(Type.Known.STRING)
			                                                           .nullable(true)
			                                                           .build(),
			                      "reason", Schema.builder().type(Type.Known.STRING)
			                                      .enum_("NOT_BOOK_IMAGE", "NO_ISBN", "BLURRY_OR_DAMAGED",
			                                             "BARCODE_ONLY_NO_NUMBER", "MULTIPLE_ISBN_AMBIGUOUS",
			                                             "INVALID_LENGTH", "UNKNOWN")
			                                      .nullable(true)
			                                      .build()
							  ))
		                      .required(
								  ImmutableList.of("analysis", "found", "isbn", "reason"))
		                      .build();

		return GenerateContentConfig.builder()
		                            .thinkingConfig(
										ThinkingConfig.builder().thinkingBudget(0))
		                            .systemInstruction(Content.fromParts(
										Part.fromText(SYSTEM_INSTRUCTION)))
		                            .temperature(0.0f)
		                            .responseMimeType("application/json")
		                            .candidateCount(1)
		                            .responseSchema(schema)
		                            .maxOutputTokens(512)
		                            .build();
	}

	private Content buildContent(MultipartFile image) {
		try {
			return Content.fromParts(
				Part.fromText(USER_PROMPT),
				Part.fromBytes(image.getBytes(), image.getContentType()));
		} catch (Exception e) {
			log.error("ISBN 추출 중 예외 발생: {}", e.getMessage(), e);
			throw new OcrProcessingException();
		}
	}

	private String parseIsbn(GenerateContentResponse response) {
		try {
			String text = response.text();
			if (text == null || text.isBlank()) {
				throw new OcrProcessingException();
			}
			IsbnResponseDto isbnResponse = objectMapper.readValue(text, IsbnResponseDto.class);
			if (isbnResponse.found()) {
				log.info("ISBN 추출 성공 - ISBN: {}, analysis: {}", isbnResponse.isbn(), isbnResponse.analysis());
				return isbnResponse.isbn();
			} else {
				log.warn("ISBN 추출 실패 - reason: {}, analysis: {}", isbnResponse.reason(), isbnResponse.analysis());
				throw IsbnExtractionFailedException.withReason(isbnResponse.reason());
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
