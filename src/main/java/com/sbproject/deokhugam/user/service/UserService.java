package com.sbproject.deokhugam.user.service;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;

public interface UserService {

    UserDto login(UserLoginRequest request);

}