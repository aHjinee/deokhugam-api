package com.sbproject.deokhugam.user.exception;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;

// User 관련 예외의 공통 부모 - UserNotFoundException, UserAlreadyExistsException 등이 상속
public class UserException extends BaseException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
