package com.sbproject.deokhugam.user.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException(String field) {
        super(ErrorCode.USER_ALREADY_EXISTS);
        addDetail("field", field);
    }

    // 정적 팩토리 메서드
    public static UserAlreadyExistsException withUsername(String username) {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("username");
        ex.addDetail("username", username);
        return ex;
    }
}
