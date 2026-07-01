package com.sbproject.deokhugam.notification.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Notification API Integration Test")
class NotificationApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    // data.sql 사용자
    private static final UUID USER_ID =
            UUID.fromString("019435e8-3d00-7a3b-8199-c6b41c80317f");

    private static final UUID OTHER_USER_ID =
            UUID.fromString("019459cb-8e20-7392-af22-582a23b8c1e9");

    // data.sql 알림
    private static final UUID NOTIFICATION_ID =
            UUID.fromString("0194a76b-1380-74c6-a8e4-8c69d777a477");

    @Nested
    @DisplayName("GET /api/notifications")
    class FindAll {

        @Test
        @DisplayName("알림 목록 조회")
        void findAll_success() throws Exception {

            mockMvc.perform(
                            get("/api/notifications")
                                    .param("userId", USER_ID.toString())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.hasNext").exists());
        }

        @Test
        @DisplayName("알림 목록 조회 - limit 적용")
        void findAll_limit() throws Exception {

            mockMvc.perform(
                            get("/api/notifications")
                                    .param("userId", USER_ID.toString())
                                    .param("limit", "2")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.size").value(2));
        }

        @Test
        @DisplayName("알림 목록 조회 - cursor 조회")
        void findAll_cursor() throws Exception {

            mockMvc.perform(
                            get("/api/notifications")
                                    .param("userId", USER_ID.toString())
                                    .param("cursor", NOTIFICATION_ID.toString())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("알림 목록 조회 - 존재하지 않는 사용자")
        void findAll_empty() throws Exception {

            mockMvc.perform(
                            get("/api/notifications")
                                    .param("userId", UUID.randomUUID().toString())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.hasNext").value(false));
        }

        @Test
        @DisplayName("알림 목록 조회 - 기본 limit")
        void findAll_defaultLimit() throws Exception {

            mockMvc.perform(
                            get("/api/notifications")
                                    .param("userId", USER_ID.toString())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(20));
        }
    }

    @Nested
    @DisplayName("PATCH /api/notifications/{notificationId}")
    class UpdateReadStatus {

        @Test
        @DisplayName("읽음 처리 성공")
        void update_success() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/{notificationId}", NOTIFICATION_ID)
                                    .header("Deokhugam-Request-User-ID", USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                              "confirmed": true
                                            }
                                            """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(NOTIFICATION_ID.toString()))
                    .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
                    .andExpect(jsonPath("$.confirmed").value(true))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.reviewContent").isNotEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 알림")
        void update_notFound() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/{notificationId}", UUID.randomUUID())
                                    .header("Deokhugam-Request-User-ID", USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                              "confirmed": true
                                            }
                                            """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOTIFICATION_NOT_FOUND"))
                    .andExpect(jsonPath("$.details.id").exists());
        }

        @Test
        @DisplayName("다른 사용자는 수정할 수 없다")
        void update_accessDenied() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/{notificationId}", NOTIFICATION_ID)
                                    .header("Deokhugam-Request-User-ID", OTHER_USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                              "confirmed": true
                                            }
                                            """))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("NOTIFICATION_ACCESS_DENIED"));
        }
        @Test
        @DisplayName("헤더 없이 요청")
        void update_withoutHeader() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/{notificationId}", NOTIFICATION_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                              "confirmed": true
                                            }
                                            """))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("NOTIFICATION_ACCESS_DENIED"));
        }

        @Test
        @DisplayName("confirmed 요청")
        void update_falseRequest() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/{notificationId}", NOTIFICATION_ID)
                                    .header("Deokhugam-Request-User-ID", USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                              "confirmed": false
                                            }
                                            """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.confirmed").exists());
        }
    }

    @Nested
    @DisplayName("PATCH /api/notifications/read-all")
    class UpdateReadAllStatus {

        @Test
        @DisplayName("전체 읽음 처리 성공")
        void updateReadAll_success() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/read-all")
                                    .header("Deokhugam-Request-User-ID", USER_ID)
                    )
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("읽지 않은 알림이 없어도 성공")
        void updateReadAll_alreadyConfirmed() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/read-all")
                                    .header("Deokhugam-Request-User-ID", USER_ID)
                    )
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 사용자")
        void updateReadAll_userNotFound() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/read-all")
                                    .header("Deokhugam-Request-User-ID", UUID.randomUUID())
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
        }

        @Test
        @DisplayName("헤더 없이 요청")
        void updateReadAll_withoutHeader() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/read-all")
                                    .header(
                                            "Deokhugam-Request-User-ID",
                                            "019435e8-3d00-7a3b-8199-c6b41c80317f"
                                    )
                    )
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("전체 읽음 처리 후 다시 조회")
        void updateReadAll_thenFindAll() throws Exception {

            mockMvc.perform(
                            patch("/api/notifications/read-all")
                                    .header("Deokhugam-Request-User-ID", USER_ID)
                    )
                    .andExpect(status().isNoContent());

            mockMvc.perform(
                            get("/api/notifications")
                                    .param("userId", USER_ID.toString())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }
}