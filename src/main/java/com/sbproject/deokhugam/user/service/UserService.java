package com.sbproject.deokhugam.user.service;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.dto.UserRegisterRequest;

import java.util.UUID;

public interface UserService {

    UserDto login(UserLoginRequest request);

    UserDto register(UserRegisterRequest request);

    UserDto findById(UUID userId);

    UserDto update(UUID userId, String nickname);

    void delete(UUID userId);

    void hardDelete(UUID userId);

}