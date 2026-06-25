package com.sbproject.deokhugam.user.dto;

import com.sbproject.deokhugam.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserDto {

    private UUID id;
    private String email;
    private String nickname;
    private Instant createdAt;

    public UserDto() {
    }

    public UserDto(UUID id, String email, String nickname, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}