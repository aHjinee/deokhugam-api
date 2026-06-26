package com.sbproject.deokhugam.notification.repository;

import com.sbproject.deokhugam.notification.entity.NotificationTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationTemp, UUID> {
}
