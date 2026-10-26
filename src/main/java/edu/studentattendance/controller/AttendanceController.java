package edu.studentattendance.controller;

import edu.studentattendance.dto.AttendanceBatchRequest;
import edu.studentattendance.dto.AttendanceResponse;
import edu.studentattendance.entity.AttendanceStatus;
import edu.studentattendance.entity.Role;
import edu.studentattendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendance;
    public AttendanceController(AttendanceService attendance) { this.attendance = attendance; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public List<AttendanceResponse> history(Authentication auth,
            @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) Long studentId, @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return attendance.history(auth.getName(), role, from, to, studentId, classId, subjectId);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<AttendanceResponse> mark(@Valid @RequestBody AttendanceBatchRequest request, Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return attendance.mark(request, auth.getName(), role);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public AttendanceResponse update(@PathVariable Long id, @RequestParam AttendanceStatus status, Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return attendance.update(id, status, auth.getName(), role);
    }
}
