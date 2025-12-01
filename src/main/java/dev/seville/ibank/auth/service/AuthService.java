package dev.seville.ibank.auth.service;

import dev.seville.ibank.auth.dto.AuthResponse;
import dev.seville.ibank.auth.dto.LoginDTO;
import dev.seville.ibank.auth.dto.RegistrationDTO;
import dev.seville.ibank.auth.entity.RefreshToken;
import dev.seville.ibank.auth.repository.TokenRepository;
import dev.seville.ibank.config.JwtService;
import dev.seville.ibank.user.entity.Role;
import dev.seville.ibank.user.entity.User;
import dev.seville.ibank.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepo, TokenRepository tokenRepo, PasswordEncoder encoder, JwtService jwtService, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
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

        User savedUser = userRepo.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(savedUser, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
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

        tokenRepo.revokeAllToken(user.getId());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String token) {
        final String username;

        username = jwtService.extractUsernameFromRefresh(token);

        User user = userRepo.findByEmail(username).orElseThrow();
        RefreshToken refreshToken = tokenRepo.findByToken(token).orElseThrow();

        if (refreshToken.isExpired() || refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh Token invalid");
        }

        // revoke old refresh token
        refreshToken.setExpired(true);
        refreshToken.setRevoked(true);
        tokenRepo.save(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(refreshToken);
        token.setExpired(false);
        token.setRevoked(false);

        tokenRepo.save(token);
    }
}
