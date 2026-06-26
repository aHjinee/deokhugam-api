package com.sbproject.deokhugam.comments.exception;

import java.util.UUID;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class CommentNotFoundException extends BaseException {

	public CommentNotFoundException(UUID commentId) {
		super(ErrorCode.COMMENT_NOT_FOUND);
		addDetail("commentId", commentId);
	}
}
