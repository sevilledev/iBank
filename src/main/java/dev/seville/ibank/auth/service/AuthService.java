package dev.seville.ibank.auth.service;

import dev.seville.ibank.auth.dto.UserRegistrationDTO;
import dev.seville.ibank.user.entity.Role;
import dev.seville.ibank.user.entity.User;
import dev.seville.ibank.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public User registerUser(UserRegistrationDTO userdto) {
        if (userRepo.findByEmail(userdto.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(userdto.username());
        user.setEmail(userdto.email());
        user.setPassword(encoder.encode(userdto.password()));
        user.setRole(Role.CUSTOMER);

        return userRepo.save(user);
    }
}
