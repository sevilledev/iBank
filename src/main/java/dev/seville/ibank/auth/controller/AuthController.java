package dev.seville.ibank.auth.controller;

import dev.seville.ibank.auth.dto.AuthResponse;
import dev.seville.ibank.auth.dto.LoginDTO;
import dev.seville.ibank.auth.dto.RegistrationDTO;
import dev.seville.ibank.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegistrationDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginDTO request) {
        return ResponseEntity.ok(service.login(request));
    }
}
