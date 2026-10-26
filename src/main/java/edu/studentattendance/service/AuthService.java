package edu.studentattendance.service;

import edu.studentattendance.config.JwtService;
import edu.studentattendance.dto.LoginRequest;
import edu.studentattendance.dto.LoginResponse;
import edu.studentattendance.dto.RegistrationRequest;
import edu.studentattendance.entity.AppUser;
import edu.studentattendance.entity.Role;
import edu.studentattendance.entity.Student;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.StudentRepository;
import edu.studentattendance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final StudentRepository students;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    public AuthService(UserRepository users, StudentRepository students, PasswordEncoder encoder, JwtService jwtService) {
        this.users = users; this.students = students; this.encoder = encoder; this.jwtService = jwtService;
    }
    public LoginResponse login(LoginRequest request) {
        String username = request.username().trim();
        AppUser user = users.findByUsernameIgnoreCase(username)
                .or(() -> students.findByEmailIgnoreCase(username)
                        .flatMap(student -> users.findByUsernameIgnoreCase(student.getStudentCode())))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!encoder.matches(request.password(), user.getPassword()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        return new LoginResponse(jwtService.createToken(user), user.getUsername(), user.getRole());
    }

    @Transactional
    public LoginResponse register(RegistrationRequest request) {
        String studentCode = request.studentCode().trim();
        String email = request.email().trim().toLowerCase();
        if (students.existsByStudentCodeIgnoreCase(studentCode)
                || students.existsByEmailIgnoreCase(email)
                || users.existsByUsernameIgnoreCase(studentCode))
            throw new ApiException(HttpStatus.CONFLICT, "Student code or email already exists");

        Student student = students.save(new Student(studentCode, request.name().trim(), email,
                request.phone() == null || request.phone().isBlank() ? null : request.phone().trim(),
                request.department().trim(), request.year(), null));
        AppUser user = users.save(new AppUser(student.getStudentCode(),
                encoder.encode(request.password()), Role.STUDENT));
        return new LoginResponse(jwtService.createToken(user), user.getUsername(), user.getRole());
    }
}
