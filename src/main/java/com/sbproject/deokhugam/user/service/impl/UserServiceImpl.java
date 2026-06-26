package com.sbproject.deokhugam.user.service.impl;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.dto.UserRegisterRequest;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.exception.InvalidCredentialsException;
import com.sbproject.deokhugam.user.exception.UserAlreadyExistsException;
import com.sbproject.deokhugam.user.exception.UserNotFoundException;
import com.sbproject.deokhugam.user.repository.UserRepository;
import com.sbproject.deokhugam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return UserDto.from(user);
    }

    @Override
    @Transactional
    public UserDto register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("email");
        }

        User user = User.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .build();

        return UserDto.from(userRepository.save(user));
    }

    @Override
    public UserDto findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return UserDto.from(user);
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateNickname(nickname);

        return UserDto.from(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void hardDelete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        userRepository.hardDeleteById(userId);
    }
}