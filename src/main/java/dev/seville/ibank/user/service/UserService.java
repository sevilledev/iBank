package dev.seville.ibank.user.service;

import dev.seville.ibank.user.dto.UserResponseDTO;
import dev.seville.ibank.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepo.findAll().stream().
                map(u -> new UserResponseDTO(u.getId(), u.getUsername(), u.getEmail(), u.getRole())).toList();
    }
}
