package com.sbproject.deokhugam.notification.exception;

import com.sbproject.deokhugam.common.exception.BaseException;
import com.sbproject.deokhugam.common.exception.ErrorCode;


public class NotificationException extends BaseException {
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
    public NotificationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
