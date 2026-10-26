package edu.studentattendance.controller;

import edu.studentattendance.service.AttendanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {
    private final AttendanceService attendance;
    public DashboardController(AttendanceService attendance) { this.attendance = attendance; }
    @GetMapping public Map<String, Object> dashboard() { return attendance.dashboard(LocalDate.now()); }
}
