package com.sbproject.deokhugam.user.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

import java.util.UUID;

public class UserNotFoundException extends UserException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public static UserNotFoundException withId(UUID id) {
        UserNotFoundException ex = new UserNotFoundException();
        ex.addDetail("id", id);
        return ex;
    }

    public static UserNotFoundException withUsername(String username) {
        UserNotFoundException ex = new UserNotFoundException();
        ex.addDetail("username", username);
        return ex;
    }
}
