package edu.studentattendance.controller;

import edu.studentattendance.dto.TeacherRequest;
import edu.studentattendance.dto.TeacherResponse;
import edu.studentattendance.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {
    private final TeacherService teachers;
    public TeacherController(TeacherService teachers) { this.teachers = teachers; }
    @GetMapping public List<TeacherResponse> list() { return teachers.list(); }
    @GetMapping("/{id}") public TeacherResponse get(@PathVariable Long id) { return teachers.get(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse create(@Valid @RequestBody TeacherRequest request) { return teachers.create(request); }
    @PutMapping("/{id}")
    public TeacherResponse update(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) { return teachers.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { teachers.delete(id); }
}
