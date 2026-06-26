package com.sbproject.deokhugam.comments.service;

import java.util.UUID;

import com.sbproject.deokhugam.comments.dto.CommentDto;

public interface CommentService {

	CommentDto findComment(UUID commentId);
}
