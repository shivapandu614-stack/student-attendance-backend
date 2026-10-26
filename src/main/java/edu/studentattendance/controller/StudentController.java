package edu.studentattendance.controller;

import edu.studentattendance.dto.StudentRequest;
import edu.studentattendance.dto.StudentResponse;
import edu.studentattendance.entity.Role;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.StudentRepository;
import edu.studentattendance.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService students;
    private final StudentRepository studentRepository;
    public StudentController(StudentService students, StudentRepository studentRepository) {
        this.students = students; this.studentRepository = studentRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<StudentResponse> list(Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return students.list(auth.getName(), role);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponse me(Authentication authentication) {
        var student = studentRepository.findByStudentCodeIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student profile not found"));
        return students.get(student.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse get(@PathVariable Long id) { return students.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse create(@Valid @RequestBody StudentRequest request) { return students.create(request); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return students.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { students.delete(id); }
}
