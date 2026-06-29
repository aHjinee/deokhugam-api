package com.sbproject.deokhugam.notification.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;
import com.sbproject.deokhugam.user.exception.UserException;

import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException() {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    public static NotificationNotFoundException withId(UUID id) {
        NotificationNotFoundException ex = new NotificationNotFoundException();
        ex.addDetail("id", id);
        return ex;
    }

}
