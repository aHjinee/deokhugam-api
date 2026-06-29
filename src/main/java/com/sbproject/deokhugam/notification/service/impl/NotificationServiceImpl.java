package com.sbproject.deokhugam.notification.service.impl;

import com.sbproject.deokhugam.common.dto.SlicePageResponse;
import com.sbproject.deokhugam.notification.dto.NotificationDto;
import com.sbproject.deokhugam.notification.dto.NotificationUpdateRequest;
import com.sbproject.deokhugam.notification.entity.Notification;
import com.sbproject.deokhugam.notification.entity.NotificationType;
import com.sbproject.deokhugam.notification.exception.NotificationAccessDeniedException;
import com.sbproject.deokhugam.notification.exception.NotificationNotFoundException;
import com.sbproject.deokhugam.notification.mapper.NotificationMapper;
import com.sbproject.deokhugam.notification.repository.NotificationQueryRepository;
import com.sbproject.deokhugam.notification.repository.NotificationRepository;
import com.sbproject.deokhugam.notification.service.NotificationService;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    @Override
    public SlicePageResponse<NotificationDto> findAllByUserId(UUID userId, String cursor, Instant after, int limit) {
        UUID cursorId = null;

        if (cursor != null && !cursor.isBlank()) {
            cursorId = UUID.fromString(cursor);
        }

        Slice<Notification> slice = notificationQueryRepository.findAllByUserId(userId, cursorId, after, limit);

        List<NotificationDto> content = slice.getContent()
                .stream()
                .map(notificationMapper::toDto)
                .toList();

        String nextCursor = null;
        Instant nextAfter = null;

        if (slice.hasNext() && !slice.getContent().isEmpty()) {
            Notification last = slice.getContent()
                    .get(slice.getContent().size() - 1);

            nextCursor = last.getId().toString();
            nextAfter = last.getCreatedAt();
        }

        return SlicePageResponse.<NotificationDto>builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(limit)
                .hasNext(slice.hasNext())
                .build();
    }

    @Transactional
    @Override
    public NotificationDto updateReadStatus(UUID notificationId, UUID deokhugamRequestUserId, NotificationUpdateRequest request) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

        if (!notification.getUser().getId().equals(deokhugamRequestUserId)) {
            throw NotificationAccessDeniedException.withuserId(deokhugamRequestUserId);
        }

        if (request.isConfirmed()) {
            notification.confirm();
        }

        return notificationMapper.toDto(notification);
    }

    @Transactional
    @Override
    public void updateReadAllStatus(UUID deokhugamRequestUserId) {
        User user = userRepository.findById(deokhugamRequestUserId)
                .orElseThrow(() -> UserNotFoundException.withId(deokhugamRequestUserId));

        List<Notification> notifications = notificationRepository.findByUserIdAndConfirmedFalse(user.getId());
        for (Notification notification : notifications) {
            notification.confirm();
        }
    }

    @Transactional
    @Override
    public NotificationDto create(NotificationType type, UUID receiverId, UUID actorId, UUID reviewId) {

        String message = createMessage(type, actorId);

        User receiveUser = userRepository.findById(receiverId)
                .orElseThrow(() -> UserNotFoundException.withId(receiverId));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

        Notification notification = Notification.builder()
                .type(type)
                .user(receiveUser)
                .review(review)
                .reviewContent(review.getContent())
                .message(message)
                .confirmed(false)
                .build();

        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    private String createMessage(NotificationType type, UUID actorId) {

        String nickname = actorId == null
                ? null
                : userRepository.findById(actorId).orElseThrow(() -> UserNotFoundException.withId(actorId)).getNickname();

        return switch (type) {
            case REVIEW_LIKE -> nickname + "님이 회원님의 리뷰를 좋아합니다.";

            case REVIEW_COMMENT -> nickname + "님이 회원님의 리뷰에 댓글을 남겼습니다.";

            case POPULAR_DAILY -> "회원님의 리뷰가 일간 인기 리뷰 TOP10에 선정되었습니다.";

            case POPULAR_WEEKLY -> "회원님의 리뷰가 주간 인기 리뷰 TOP10에 선정되었습니다.";

            case POPULAR_MONTHLY -> "회원님의 리뷰가 월간 인기 리뷰 TOP10에 선정되었습니다.";

            case POPULAR_ALL_TIME -> "회원님의 리뷰가 전체 인기 리뷰 TOP10에 선정되었습니다.";
        };
    }


}
