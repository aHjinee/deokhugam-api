package com.sbproject.deokhugam.user.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

public class InvalidCredentialsException extends UserException {

    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }

    public static InvalidCredentialsException withUsername(String username) {
        InvalidCredentialsException ex = new InvalidCredentialsException();
        ex.addDetail("username", username);
        return ex;
    }
}
