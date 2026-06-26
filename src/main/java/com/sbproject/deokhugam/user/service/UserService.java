package com.sbproject.deokhugam.user.service;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.dto.UserRegisterRequest;

public interface UserService {

    UserDto login(UserLoginRequest request);

    UserDto register(UserRegisterRequest request);

}