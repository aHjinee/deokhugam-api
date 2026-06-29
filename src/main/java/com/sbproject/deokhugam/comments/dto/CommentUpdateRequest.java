package com.sbproject.deokhugam.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
	@NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
	@Size(max = 500)
	String content
) {
}
