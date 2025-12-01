package dev.seville.ibank.auth.controller;

import dev.seville.ibank.auth.dto.AuthResponse;
import dev.seville.ibank.auth.dto.LoginDTO;
import dev.seville.ibank.auth.dto.RefreshTokenDTO;
import dev.seville.ibank.auth.dto.RegistrationDTO;
import dev.seville.ibank.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegistrationDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }
}
