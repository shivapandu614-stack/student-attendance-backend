package edu.studentattendance.controller;

import edu.studentattendance.dto.ReportResponse;
import edu.studentattendance.service.AttendanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import edu.studentattendance.entity.Role;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
public class ReportController {
    private final AttendanceService attendance;
    public ReportController(AttendanceService attendance) { this.attendance = attendance; }
    @GetMapping
    public List<ReportResponse> report(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                       @RequestParam(required = false) Long classId, @RequestParam(required = false) Long subjectId,
                                       @RequestParam(required = false) String studentCode, Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        return attendance.reports(from, to, classId, subjectId, studentCode, auth.getName(), role);
    }
    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<String> csv(@RequestParam(required = false) LocalDate from,
                                      @RequestParam(required = false) LocalDate to,
                                      @RequestParam(required = false) Long classId,
                                      @RequestParam(required = false) Long subjectId,
                                      @RequestParam(required = false) String studentCode, Authentication auth) {
        Role role = Role.valueOf(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        List<ReportResponse> rows = attendance.reports(from, to, classId, subjectId, studentCode, auth.getName(), role);
        StringBuilder csv = new StringBuilder("Student ID,Student Name,Class,Total Classes,Attended,Absent,Late,Attendance Percentage\n");
        for (ReportResponse row : rows) csv.append(csv(row.studentCode())).append(',')
                .append(csv(row.studentName())).append(',').append(csv(row.className())).append(',')
                .append(row.totalClasses()).append(',').append(row.attended()).append(',')
                .append(row.absent()).append(',').append(row.late()).append(',')
                .append(row.percentage()).append("%\n");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance-report.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8")).body(csv.toString());
    }
    private String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}
