package edu.studentattendance.service;

import edu.studentattendance.dto.TeacherRequest;
import edu.studentattendance.dto.TeacherResponse;
import edu.studentattendance.entity.AppUser;
import edu.studentattendance.entity.Role;
import edu.studentattendance.entity.Teacher;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.TeacherRepository;
import edu.studentattendance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeacherService {
    private final TeacherRepository teachers;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public TeacherService(TeacherRepository teachers, UserRepository users, PasswordEncoder encoder) {
        this.teachers = teachers; this.users = users; this.encoder = encoder;
    }
    public List<TeacherResponse> list() { return teachers.findAll().stream().map(this::response).toList(); }
    public TeacherResponse get(Long id) { return response(teachers.findById(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher not found"))); }
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        if (request.password() == null || request.password().length() < 8)
            throw new ApiException(HttpStatus.BAD_REQUEST, "A password of at least 8 characters is required");
        if (teachers.existsByTeacherCodeIgnoreCase(request.teacherCode())
                || teachers.existsByEmailIgnoreCase(request.email()) || users.existsByUsernameIgnoreCase(request.email()))
            throw new ApiException(HttpStatus.CONFLICT, "Teacher code or email already exists");
        Teacher teacher = teachers.save(new Teacher(request.teacherCode().trim(), request.name().trim(),
                request.email().trim().toLowerCase(), request.department().trim()));
        users.save(new AppUser(teacher.getEmail(), encoder.encode(request.password()), Role.TEACHER));
        return response(teacher);
    }
    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = teachers.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher not found"));
        if (!teacher.getTeacherCode().equalsIgnoreCase(request.teacherCode())
                && teachers.existsByTeacherCodeIgnoreCase(request.teacherCode()))
            throw new ApiException(HttpStatus.CONFLICT, "Teacher code already exists");
        String oldEmail = teacher.getEmail();
        String newEmail = request.email().trim().toLowerCase();
        if (!oldEmail.equalsIgnoreCase(newEmail)
                && (teachers.existsByEmailIgnoreCase(newEmail) || users.existsByUsernameIgnoreCase(newEmail)))
            throw new ApiException(HttpStatus.CONFLICT, "Teacher email already exists");
        teacher.update(request.teacherCode().trim(), request.name().trim(), newEmail, request.department().trim());
        users.findByUsernameIgnoreCase(oldEmail).ifPresent(user -> {
            user.setUsername(newEmail);
            users.save(user);
        });
        return response(teachers.save(teacher));
    }
    @Transactional
    public void delete(Long id) {
        Teacher teacher = teachers.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher not found"));
        users.findByUsernameIgnoreCase(teacher.getEmail()).ifPresent(users::delete);
        teachers.delete(teacher);
    }
    private TeacherResponse response(Teacher teacher) {
        return new TeacherResponse(teacher.getId(), teacher.getTeacherCode(), teacher.getName(), teacher.getEmail(), teacher.getDepartment());
    }
}
