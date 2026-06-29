package com.sbproject.deokhugam.comments.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
	@NotNull(message = "리뷰 ID는 필수 입력 항목입니다.")
	UUID reviewId,

	@NotNull(message = "사용자 ID는 필수 입력 항목입니다.")
	UUID userId,

	@NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
	@Size(max = 500)
	String content
) {
}
