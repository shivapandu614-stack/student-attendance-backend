package edu.studentattendance.controller;

import edu.studentattendance.dto.LoginRequest;
import edu.studentattendance.dto.LoginResponse;
import edu.studentattendance.dto.RegistrationRequest;
import edu.studentattendance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }

    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody RegistrationRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("username", authentication.getName(),
                "role", authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }
}
