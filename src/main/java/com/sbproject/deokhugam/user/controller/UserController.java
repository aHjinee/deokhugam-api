package com.sbproject.deokhugam.user.controller;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @Valid @RequestBody UserLoginRequest request
    ) {

        return ResponseEntity.ok(
                userService.login(request)
        );
    }

}