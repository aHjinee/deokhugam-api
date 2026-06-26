package com.sbproject.deokhugam.user.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class UnauthorizedAccessException extends UserException {

    public UnauthorizedAccessException() {
        super(ErrorCode.UNAUTHORIZED_ACCESS);
    }
}