package com.sbproject.deokhugam.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(min = 2, max = 50) String nickname
) {}