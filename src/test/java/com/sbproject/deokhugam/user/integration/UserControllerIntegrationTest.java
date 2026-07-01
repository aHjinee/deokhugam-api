package com.sbproject.deokhugam.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    // data.sql에 있는 기존 유저 ID (김민준)
    private static final String EXISTING_USER_ID = "019435e8-3d00-7a3b-8199-c6b41c80317f";
    // data.sql에 있는 다른 유저 ID (이서연)
    private static final String OTHER_USER_ID = "01944652-8240-7bdd-8fac-4ee446685257";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> buildRegisterRequest(String email, String nickname, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("nickname", nickname);
        request.put("password", password);
        return request;
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        Map<String, String> request = buildRegisterRequest(
                "newuser@example.com", "새유저", "password123"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.nickname").value("새유저"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail_duplicateEmail() throws Exception {
        Map<String, String> request = buildRegisterRequest(
                "minjun.kim@example.com", "다른닉네임", "password123"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 형식 오류")
    void register_fail_invalidEmail() throws Exception {
        Map<String, String> request = buildRegisterRequest(
                "not-an-email", "닉네임", "password123"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void findById_success() throws Exception {
        mockMvc.perform(get("/api/users/{userId}", EXISTING_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("minjun.kim@example.com"))
                .andExpect(jsonPath("$.nickname").value("김민준"));
    }

    @Test
    @DisplayName("사용자 조회 실패 - 존재하지 않는 ID")
    void findById_fail_notFound() throws Exception {
        String notExistingUserId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/users/{userId}", notExistingUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    void update_success() throws Exception {
        Map<String, String> request = Map.of("nickname", "새닉네임");

        mockMvc.perform(patch("/api/users/{userId}", EXISTING_USER_ID)
                        .header("Deokhugam-Request-User-ID", EXISTING_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("새닉네임"));
    }

    @Test
    @DisplayName("닉네임 수정 실패 - 권한 없음 (다른 유저 ID)")
    void update_fail_unauthorized() throws Exception {
        Map<String, String> request = Map.of("nickname", "새닉네임");

        mockMvc.perform(patch("/api/users/{userId}", EXISTING_USER_ID)
                        .header("Deokhugam-Request-User-ID", OTHER_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("논리 삭제 성공")
    void delete_success() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", EXISTING_USER_ID)
                        .header("Deokhugam-Request-User-ID", EXISTING_USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("논리 삭제 실패 - 권한 없음")
    void delete_fail_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", EXISTING_USER_ID)
                        .header("Deokhugam-Request-User-ID", OTHER_USER_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("논리 삭제 실패 - 존재하지 않는 사용자")
    void delete_fail_notFound() throws Exception {
        String notExistingUserId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/users/{userId}", notExistingUserId)
                        .header("Deokhugam-Request-User-ID", notExistingUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("물리 삭제 성공")
    void hardDelete_success() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/hard", EXISTING_USER_ID)
                        .header("Deokhugam-Request-User-ID", EXISTING_USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("물리 삭제 실패 - 존재하지 않는 사용자")
    void hardDelete_fail_notFound() throws Exception {
        String notExistingUserId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/users/{userId}/hard", notExistingUserId)
                        .header("Deokhugam-Request-User-ID", notExistingUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("물리 삭제 실패 - 권한 없음")
    void hardDelete_fail_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/hard", EXISTING_USER_ID)
                        .header("Deokhugam-Request-User-ID", OTHER_USER_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        Map<String, String> registerRequest = buildRegisterRequest(
                "logintest@example.com", "로그인테스트", "password123"
        );
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        Map<String, String> loginRequest = Map.of(
                "email", "logintest@example.com",
                "password", "password123"
        );

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("logintest@example.com"));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrongPassword() throws Exception {
        Map<String, String> registerRequest = buildRegisterRequest(
                "wrongpwtest@example.com", "비번테스트", "password123"
        );
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        Map<String, String> loginRequest = Map.of(
                "email", "wrongpwtest@example.com",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}