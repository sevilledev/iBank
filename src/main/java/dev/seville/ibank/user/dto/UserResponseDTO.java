package dev.seville.ibank.user.dto;

import dev.seville.ibank.user.entity.Role;

public record UserResponseDTO (
        Long id,
        String username,
        String email,
        Role role
) {
}
