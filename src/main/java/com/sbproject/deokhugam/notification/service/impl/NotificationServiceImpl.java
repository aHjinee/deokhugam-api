package com.sbproject.deokhugam.notification.service.impl;

import com.sbproject.deokhugam.notification.repository.NotificationRepository;
import com.sbproject.deokhugam.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
}
