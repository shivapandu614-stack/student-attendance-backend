package edu.studentattendance.service;

import edu.studentattendance.config.JwtService;
import edu.studentattendance.dto.LoginRequest;
import edu.studentattendance.dto.LoginResponse;
import edu.studentattendance.entity.AppUser;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwtService) {
        this.users = users; this.encoder = encoder; this.jwtService = jwtService;
    }
    public LoginResponse login(LoginRequest request) {
        AppUser user = users.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!encoder.matches(request.password(), user.getPassword()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        return new LoginResponse(jwtService.createToken(user), user.getUsername(), user.getRole());
    }
}
