package edu.studentattendance.controller;

import edu.studentattendance.dto.*;
import edu.studentattendance.entity.Role;
import edu.studentattendance.service.AcademicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicController {
    private final AcademicService academics;
    public AcademicController(AcademicService academics) { this.academics = academics; }
    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public List<ClassResponse> classes(Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return academics.classes(auth.getName(), role);
    }
    @PostMapping("/classes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ClassResponse createClass(@Valid @RequestBody ClassRequest request) { return academics.createClass(request); }
    @PutMapping("/classes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClassResponse updateClass(@PathVariable Long id, @Valid @RequestBody ClassRequest request) { return academics.updateClass(id, request); }
    @DeleteMapping("/classes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteClass(@PathVariable Long id) { academics.deleteClass(id); }
    @GetMapping("/subjects")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public List<SubjectResponse> subjects(Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return academics.subjects(auth.getName(), role);
    }
    @PostMapping("/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse createSubject(@Valid @RequestBody SubjectRequest request) { return academics.createSubject(request); }
    @PutMapping("/subjects/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponse updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        return academics.updateSubject(id, request);
    }
    @DeleteMapping("/subjects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubject(@PathVariable Long id) { academics.deleteSubject(id); }
}
