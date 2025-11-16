package dev.seville.ibank.auth.service;

import dev.seville.ibank.auth.dto.AuthResponse;
import dev.seville.ibank.auth.dto.LoginDTO;
import dev.seville.ibank.auth.dto.RegistrationDTO;
import dev.seville.ibank.config.JwtService;
import dev.seville.ibank.user.entity.Role;
import dev.seville.ibank.user.entity.User;
import dev.seville.ibank.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtService jwtService, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public AuthResponse register(RegistrationDTO request) {
        if (userRepo.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstname(request.firstname());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);

        userRepo.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginDTO request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
