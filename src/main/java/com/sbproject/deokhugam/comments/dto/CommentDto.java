package com.sbproject.deokhugam.comments.dto;

import java.time.Instant;
import java.util.UUID;

import com.sbproject.deokhugam.comments.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {

	private UUID id;
	private UUID reviewId;
	private UUID userId;
	private String userNickname;
	private String content;
	private Instant createdAt;
	private Instant updatedAt;

	public static CommentDto from(Comment comment) {
		return new CommentDto(
			comment.getId(),
			comment.getReview().getId(),
			comment.getUser().getId(),
			comment.getUser().getNickname(),
			comment.getContent(),
			comment.getCreatedAt(),
			comment.getUpdatedAt()
		);
	}
}
