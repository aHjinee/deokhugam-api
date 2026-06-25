package com.sbproject.deokhugam.user.service.impl;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.entity.User;
import com.sbproject.deokhugam.user.repository.UserRepository;
import com.sbproject.deokhugam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public UserDto login(UserLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return UserDto.from(user);
    }
}