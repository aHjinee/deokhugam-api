package com.sbproject.deokhugam.user.controller;

import com.sbproject.deokhugam.user.dto.UserDto;
import com.sbproject.deokhugam.user.dto.UserLoginRequest;
import com.sbproject.deokhugam.user.dto.UserRegisterRequest;
import com.sbproject.deokhugam.user.dto.UserUpdateRequest;
import com.sbproject.deokhugam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        return ResponseEntity.status(201).body(
                userService.register(request)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                userService.findById(userId)
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(
                userService.update(userId, request.nickname()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId
    ) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDelete(
            @PathVariable UUID userId
    ) {
        userService.hardDelete(userId);
        return ResponseEntity.noContent().build();
    }

}