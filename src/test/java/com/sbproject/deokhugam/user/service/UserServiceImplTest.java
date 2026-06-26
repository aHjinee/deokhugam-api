package com.sbproject.deokhugam.user.service;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.dto.UserRegisterRequest;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.InvalidCredentialsException;
import com.sbproject.deokhugam.user.exception.UserAlreadyExistsException;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;
import com.sbproject.deokhugam.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "12345678");
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .password("encodedPassword")
                .build();

        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("12345678", "encodedPassword")).willReturn(true);

        // when
        UserDto result = userService.login(request);

        // then
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void login_fail_email_not_found() {
        // given
        UserLoginRequest request = new UserLoginRequest("wrong@test.com", "12345678");
        given(userRepository.findByEmail("wrong@test.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrong_password() {
        // given
        UserLoginRequest request = new UserLoginRequest("test@test.com", "wrongPassword");
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .password("encodedPassword")
                .build();

        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        // given
        UserRegisterRequest request = new UserRegisterRequest("test@test.com", "테스터", "12345678");
        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(passwordEncoder.encode("12345678")).willReturn("encodedPassword");

        User user = User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .password("encodedPassword")
                .build();
        given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willReturn(user);

        // when
        UserDto result = userService.register(request);

        // then
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail_duplicate_email() {
        // given
        UserRegisterRequest request = new UserRegisterRequest("test@test.com", "테스터", "12345678");
        given(userRepository.existsByEmail("test@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void findById_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .password("encodedPassword")
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserDto result = userService.findById(userId);

        // then
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("사용자 조회 실패 - 존재하지 않는 사용자")
    void findById_fail_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    void update_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .password("encodedPassword")
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserDto result = userService.update(userId, "새닉네임");

        // then
        assertThat(result.getNickname()).isEqualTo("새닉네임");
    }

    @Test
    @DisplayName("닉네임 수정 실패 - 존재하지 않는 사용자")
    void update_fail_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.update(userId, "새닉네임"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("논리 삭제 성공")
    void delete_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .password("encodedPassword")
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.delete(userId);

        // then
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("논리 삭제 실패 - 존재하지 않는 사용자")
    void delete_fail_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}