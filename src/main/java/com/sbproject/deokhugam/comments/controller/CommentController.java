package com.sbproject.deokhugam.comments.controller;

import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CommentDto;
import com.sbproject.deokhugam.comments.service.CommentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	@GetMapping("/{commentId}")
	public ResponseEntity<CommentDto> findComment(@PathVariable UUID commentId) {
		return ResponseEntity.ok(commentService.findComment(commentId));
	}
}
