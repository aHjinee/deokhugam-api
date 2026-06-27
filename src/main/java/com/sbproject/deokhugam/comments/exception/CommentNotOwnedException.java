package com.sbproject.deokhugam.comments.exception;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

public class CommentNotOwnedException extends BaseException {

	public CommentNotOwnedException() {
		super(ErrorCode.COMMENT_NOT_OWNED);
	}
}
