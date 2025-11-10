package dev.seville.ibank.auth.controller;

import dev.seville.ibank.auth.dto.UserRegistrationDTO;
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
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationDTO userdto) {
        service.registerUser(userdto);
        return ResponseEntity.ok("user registered");
    }
}
