package com.sbproject.deokhugam.notification.service;

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
import com.sbproject.deokhugam.notification.service.impl.NotificationServiceImpl;
import com.sbproject.deokhugam.review.entity.Review;
import com.sbproject.deokhugam.review.exception.ReviewNotFoundException;
import com.sbproject.deokhugam.review.repository.ReviewRepository;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationQueryRepository notificationQueryRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID actorId;
    private UUID reviewId;
    private UUID notificationId;

    private User user;
    private User actor;
    private Review review;

    private Notification notification;

    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        actorId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("user@test.com")
                .nickname("수신자")
                .password("password")
                .build();

        actor = User.builder()
                .id(actorId)
                .email("actor@test.com")
                .nickname("작성자")
                .password("password")
                .build();

        review = Review.builder()
                .id(reviewId)
                .user(user)
                .content("리뷰 내용")
                .rating(5)
                .build();

        notification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent("리뷰 내용")
                .message("메시지")
                .confirmed(false)
                .type(NotificationType.REVIEW_COMMENT)
                .build();

        ReflectionTestUtils.setField(notification, "id", notificationId);
        ReflectionTestUtils.setField(notification, "createdAt", Instant.now());

        notificationDto = NotificationDto.builder()
                .id(notificationId)
                .userId(userId)
                .reviewId(reviewId)
                .reviewContent("리뷰 내용")
                .message("메시지")
                .confirmed(false)
                .build();
    }

    @Test
    @DisplayName("알림 목록 조회")
    void findAllByUserId_success() {

        Slice<Notification> slice = new SliceImpl<>(List.of(notification));

        given(notificationQueryRepository.findAllByUserId(
                userId,
                null,
                null,
                20
        )).willReturn(slice);

        given(notificationMapper.toDto(notification))
                .willReturn(notificationDto);

        SlicePageResponse<NotificationDto> result =
                notificationService.findAllByUserId(
                        userId,
                        null,
                        null,
                        20
                );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(notificationDto);
        assertThat(result.isHasNext()).isFalse();

        then(notificationQueryRepository)
                .should()
                .findAllByUserId(userId, null, null, 20);

        then(notificationMapper)
                .should()
                .toDto(notification);
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void updateReadStatus_success() {

        NotificationUpdateRequest request =
                NotificationUpdateRequest.builder()
                        .confirmed(true)
                        .build();

        given(notificationRepository.findById(notificationId))
                .willReturn(Optional.of(notification));

        given(notificationMapper.toDto(notification))
                .willReturn(notificationDto);

        NotificationDto result =
                notificationService.updateReadStatus(
                        notificationId,
                        userId,
                        request
                );

        assertThat(notification.isConfirmed()).isTrue();
        assertThat(result).isEqualTo(notificationDto);

        then(notificationRepository)
                .should()
                .findById(notificationId);

        then(notificationMapper)
                .should()
                .toDto(notification);
    }

    @Test
    @DisplayName("없는 알림 읽음 처리")
    void updateReadStatus_notFound() {

        given(notificationRepository.findById(notificationId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                notificationService.updateReadStatus(
                        notificationId,
                        userId,
                        NotificationUpdateRequest.builder()
                                .confirmed(true)
                                .build()
                )
        ).isInstanceOf(NotificationNotFoundException.class);

        then(notificationMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("다른 사람 알림 읽음 처리")
    void updateReadStatus_accessDenied() {

        UUID anotherUser = UUID.randomUUID();

        given(notificationRepository.findById(notificationId))
                .willReturn(Optional.of(notification));

        assertThatThrownBy(() ->
                notificationService.updateReadStatus(
                        notificationId,
                        anotherUser,
                        NotificationUpdateRequest.builder()
                                .confirmed(true)
                                .build()
                )
        ).isInstanceOf(NotificationAccessDeniedException.class);

        then(notificationMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("모든 알림 읽음 처리")
    void updateReadAllStatus_success() {

        Notification notification2 = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent("리뷰 내용2")
                .message("메시지2")
                .confirmed(false)
                .type(NotificationType.REVIEW_LIKE)
                .build();

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(notificationRepository.findByUserIdAndConfirmedFalse(userId))
                .willReturn(List.of(notification, notification2));

        // when
        notificationService.updateReadAllStatus(userId);

        // then
        assertThat(notification.isConfirmed()).isTrue();
        assertThat(notification2.isConfirmed()).isTrue();

        then(userRepository)
                .should()
                .findById(userId);

        then(notificationRepository)
                .should()
                .findByUserIdAndConfirmedFalse(userId);
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 - 사용자 없음")
    void updateReadAllStatus_userNotFound() {

        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                notificationService.updateReadAllStatus(userId)
        ).isInstanceOf(UserNotFoundException.class);

        then(notificationRepository)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("좋아요 알림 생성")
    void create_reviewLike_success() {

        Notification savedNotification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent(review.getContent())
                .message(actor.getNickname() + "님이 회원님의 리뷰를 좋아합니다.")
                .confirmed(false)
                .type(NotificationType.REVIEW_LIKE)
                .build();

        ReflectionTestUtils.setField(savedNotification, "id", notificationId);

        NotificationDto dto = NotificationDto.builder()
                .id(notificationId)
                .userId(userId)
                .reviewId(reviewId)
                .reviewContent(review.getContent())
                .message(actor.getNickname() + "님이 회원님의 리뷰를 좋아합니다.")
                .confirmed(false)
                .build();

        given(userRepository.findById(actorId))
                .willReturn(Optional.of(actor));

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

        given(notificationRepository.save(any(Notification.class)))
                .willReturn(savedNotification);

        given(notificationMapper.toDto(savedNotification))
                .willReturn(dto);

        NotificationDto result =
                notificationService.create(
                        NotificationType.REVIEW_LIKE,
                        userId,
                        actorId,
                        reviewId
                );

        assertThat(result).isEqualTo(dto);

        then(notificationRepository)
                .should()
                .save(any(Notification.class));
    }

    @Test
    @DisplayName("댓글 알림 생성")
    void create_reviewComment_success() {

        Notification savedNotification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent(review.getContent())
                .message(actor.getNickname() + "님이 회원님의 리뷰에 댓글을 남겼습니다.")
                .confirmed(false)
                .type(NotificationType.REVIEW_COMMENT)
                .build();

        ReflectionTestUtils.setField(savedNotification, "id", notificationId);

        NotificationDto dto = NotificationDto.builder()
                .id(notificationId)
                .userId(userId)
                .reviewId(reviewId)
                .reviewContent(review.getContent())
                .message(actor.getNickname() + "님이 회원님의 리뷰에 댓글을 남겼습니다.")
                .confirmed(false)
                .build();

        given(userRepository.findById(actorId))
                .willReturn(Optional.of(actor));

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

        given(notificationRepository.save(any(Notification.class)))
                .willReturn(savedNotification);

        given(notificationMapper.toDto(savedNotification))
                .willReturn(dto);

        NotificationDto result =
                notificationService.create(
                        NotificationType.REVIEW_COMMENT,
                        userId,
                        actorId,
                        reviewId
                );

        assertThat(result).isEqualTo(dto);
    }
    @Test
    @DisplayName("일간 인기 리뷰 알림 생성")
    void create_popularDaily_success() {

        Notification savedNotification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent(review.getContent())
                .message("회원님의 리뷰가 일간 인기 리뷰 TOP10에 선정되었습니다.")
                .confirmed(false)
                .type(NotificationType.POPULAR_DAILY)
                .build();

        ReflectionTestUtils.setField(savedNotification, "id", notificationId);

        NotificationDto dto = NotificationDto.builder()
                .id(notificationId)
                .userId(userId)
                .reviewId(reviewId)
                .reviewContent(review.getContent())
                .message("회원님의 리뷰가 일간 인기 리뷰 TOP10에 선정되었습니다.")
                .confirmed(false)
                .build();

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

        given(notificationRepository.save(any(Notification.class)))
                .willReturn(savedNotification);

        given(notificationMapper.toDto(savedNotification))
                .willReturn(dto);

        NotificationDto result = notificationService.create(
                NotificationType.POPULAR_DAILY,
                userId,
                null,
                reviewId
        );

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("주간 인기 리뷰 알림 생성")
    void create_popularWeekly_success() {

        Notification savedNotification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent(review.getContent())
                .message("회원님의 리뷰가 주간 인기 리뷰 TOP10에 선정되었습니다.")
                .confirmed(false)
                .type(NotificationType.POPULAR_WEEKLY)
                .build();

        ReflectionTestUtils.setField(savedNotification, "id", notificationId);

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

        given(notificationRepository.save(any(Notification.class)))
                .willReturn(savedNotification);

        given(notificationMapper.toDto(savedNotification))
                .willReturn(notificationDto);

        notificationService.create(
                NotificationType.POPULAR_WEEKLY,
                userId,
                null,
                reviewId
        );

        then(notificationRepository)
                .should()
                .save(any(Notification.class));
    }

    @Test
    @DisplayName("월간 인기 리뷰 알림 생성")
    void create_popularMonthly_success() {

        Notification savedNotification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent(review.getContent())
                .message("회원님의 리뷰가 월간 인기 리뷰 TOP10에 선정되었습니다.")
                .confirmed(false)
                .type(NotificationType.POPULAR_MONTHLY)
                .build();

        ReflectionTestUtils.setField(savedNotification, "id", notificationId);

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

        given(notificationRepository.save(any(Notification.class)))
                .willReturn(savedNotification);

        given(notificationMapper.toDto(savedNotification))
                .willReturn(notificationDto);

        notificationService.create(
                NotificationType.POPULAR_MONTHLY,
                userId,
                null,
                reviewId
        );

        then(notificationRepository)
                .should()
                .save(any(Notification.class));
    }

    @Test
    @DisplayName("전체 인기 리뷰 알림 생성")
    void create_popularAllTime_success() {

        Notification savedNotification = Notification.builder()
                .user(user)
                .review(review)
                .reviewContent(review.getContent())
                .message("회원님의 리뷰가 전체 인기 리뷰 TOP10에 선정되었습니다.")
                .confirmed(false)
                .type(NotificationType.POPULAR_ALL_TIME)
                .build();

        ReflectionTestUtils.setField(savedNotification, "id", notificationId);

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

        given(notificationRepository.save(any(Notification.class)))
                .willReturn(savedNotification);

        given(notificationMapper.toDto(savedNotification))
                .willReturn(notificationDto);

        notificationService.create(
                NotificationType.POPULAR_ALL_TIME,
                userId,
                null,
                reviewId
        );

        then(notificationRepository)
                .should()
                .save(any(Notification.class));
    }

    @Test
    @DisplayName("알림 생성 - 수신자 없음")
    void create_receiverNotFound() {

        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                notificationService.create(
                        NotificationType.POPULAR_DAILY,
                        userId,
                        null,
                        reviewId
                )
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("알림 생성 - 행위자 없음")
    void create_actorNotFound() {

        given(userRepository.findById(actorId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                notificationService.create(
                        NotificationType.REVIEW_LIKE,
                        userId,
                        actorId,
                        reviewId
                )
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("알림 생성 - 리뷰 없음")
    void create_reviewNotFound() {

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(reviewRepository.findById(reviewId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                notificationService.create(
                        NotificationType.POPULAR_DAILY,
                        userId,
                        null,
                        reviewId
                )
        ).isInstanceOf(ReviewNotFoundException.class);
    }
}