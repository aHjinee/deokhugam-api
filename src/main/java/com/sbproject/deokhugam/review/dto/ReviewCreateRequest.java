package com.sbproject.deokhugam.review.dto;

import java.util.UUID;

import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

	@NotNull(message = "책 ID는 필수입니다")
	private UUID bookId;

	@NotNull(message = "작성자 ID는 필수입니다")
	private UUID userId;

	@NotBlank(message = "내용은 필수입니다")
	@Size(max = 5000, message = "내용은 5000자 이하여야 합니다")
	private String content;

	@NotNull
	@Min(value = 1, message = "평점은 최소 1점입니다")
	@Max(value = 5, message = "평점은 최대 5점입니다")
	private Integer rating;
}
