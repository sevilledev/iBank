package dev.seville.ibank.auth.dto;

public record AuthResponse (
        String accessToken,
        String refreshToken
) {}
