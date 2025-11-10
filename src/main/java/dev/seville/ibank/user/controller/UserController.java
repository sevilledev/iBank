package dev.seville.ibank.user.controller;

import dev.seville.ibank.user.dto.UserResponseDTO;
import dev.seville.ibank.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return service.getAllUsers();
    }
}
