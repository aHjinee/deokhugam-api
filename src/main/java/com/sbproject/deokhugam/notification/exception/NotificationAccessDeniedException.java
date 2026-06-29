package com.sbproject.deokhugam.notification.exception;

import com.sbproject.deokhugam.common.exception.ErrorCode;

import java.util.UUID;

public class NotificationAccessDeniedException extends NotificationException {

    public NotificationAccessDeniedException() {
        super(ErrorCode.NOTIFICATION_ACCESS_DENIED);
    }

    public static NotificationAccessDeniedException withuserId(UUID userId) {
        NotificationAccessDeniedException ex = new NotificationAccessDeniedException();
        ex.addDetail("userId", userId);
        return ex;
    }

}
