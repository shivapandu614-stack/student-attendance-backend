package edu.studentattendance.service;

import edu.studentattendance.dto.*;
import edu.studentattendance.entity.*;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private final AttendanceRepository attendance;
    private final StudentRepository students;
    private final TeacherRepository teachers;
    private final ClassRepository classes;
    private final LookupService lookup;
    public AttendanceService(AttendanceRepository attendance, StudentRepository students, TeacherRepository teachers,
                             ClassRepository classes, LookupService lookup) {
        this.attendance = attendance; this.students = students; this.teachers = teachers; this.classes = classes; this.lookup = lookup;
    }

    @Transactional
    public List<AttendanceResponse> mark(AttendanceBatchRequest request, String username, Role role) {
        Subject subject = lookup.subject(request.subjectId());
        Teacher teacher = authorizedTeacher(subject, username, role);
        Set<Long> studentIds = new HashSet<>();
        for (var entry : request.entries()) {
            if (!studentIds.add(entry.studentId()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "A student can appear only once in an attendance request");
            Student student = lookup.student(entry.studentId());
            if (student.getSchoolClass() == null || !Objects.equals(student.getSchoolClass().getId(), subject.getSchoolClass().getId()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "All students must belong to the selected subject's class");
            if (attendance.existsByStudent_IdAndSubject_IdAndDate(student.getId(), subject.getId(), request.date()))
                throw new ApiException(HttpStatus.CONFLICT, "Attendance already exists for " + student.getStudentCode() + " on " + request.date());
        }
        return request.entries().stream().map(entry -> {
            Attendance saved = attendance.save(new Attendance(lookup.student(entry.studentId()), subject, teacher, request.date(), entry.status()));
            return response(saved);
        }).toList();
    }

    @Transactional
    public AttendanceResponse update(Long id, AttendanceStatus status, String username, Role role) {
        Attendance record = attendance.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attendance record not found"));
        authorizedTeacher(record.getSubject(), username, role);
        record.setStatus(status);
        return response(attendance.save(record));
    }

    public List<AttendanceResponse> history(String username, Role role, LocalDate from, LocalDate to,
                                            Long studentId, Long classId, Long subjectId) {
        List<Attendance> records = role == Role.STUDENT
                ? attendance.findByStudent_StudentCodeIgnoreCaseOrderByDateDesc(username)
                : attendance.findAllByOrderByDateDesc();
        if (role == Role.TEACHER) {
            records = records.stream().filter(row -> row.getSubject().getTeacher().getEmail().equalsIgnoreCase(username)).toList();
        }
        return records.stream()
                .filter(row -> from == null || !row.getDate().isBefore(from))
                .filter(row -> to == null || !row.getDate().isAfter(to))
                .filter(row -> studentId == null || row.getStudent().getId().equals(studentId))
                .filter(row -> classId == null || row.getSubject().getSchoolClass().getId().equals(classId))
                .filter(row -> subjectId == null || row.getSubject().getId().equals(subjectId))
                .map(this::response).toList();
    }

    public List<ReportResponse> reports(LocalDate from, LocalDate to, Long classId, Long subjectId,
                                        String studentCode, String username, Role role) {
        List<Attendance> rows = attendance.findAllByOrderByDateDesc().stream()
                .filter(row -> role != Role.TEACHER || row.getSubject().getTeacher().getEmail().equalsIgnoreCase(username))
                .filter(row -> from == null || !row.getDate().isBefore(from))
                .filter(row -> to == null || !row.getDate().isAfter(to))
                .filter(row -> classId == null || row.getSubject().getSchoolClass().getId().equals(classId))
                .filter(row -> subjectId == null || row.getSubject().getId().equals(subjectId))
                .filter(row -> studentCode == null || row.getStudent().getStudentCode().equalsIgnoreCase(studentCode))
                .toList();
        Map<Student, List<Attendance>> byStudent = rows.stream().collect(Collectors.groupingBy(row -> row.getStudent(), LinkedHashMap::new, Collectors.toList()));
        return byStudent.entrySet().stream().map(entry -> {
            List<Attendance> studentRows = entry.getValue();
            long attended = studentRows.stream().filter(row -> row.getStatus() != AttendanceStatus.ABSENT).count();
            long late = studentRows.stream().filter(row -> row.getStatus() == AttendanceStatus.LATE).count();
            long absent = studentRows.stream().filter(row -> row.getStatus() == AttendanceStatus.ABSENT).count();
            Student student = entry.getKey();
            return new ReportResponse(student.getStudentCode(), student.getName(),
                    student.getSchoolClass() == null ? "Unassigned" : student.getSchoolClass().getName(),
                    studentRows.size(), attended, absent, late,
                    studentRows.isEmpty() ? 0 : Math.round(attended * 10000.0 / studentRows.size()) / 100.0);
        }).toList();
    }

    public Map<String, Object> dashboard(LocalDate date) {
        List<Attendance> today = attendance.findAllByOrderByDateDesc().stream().filter(row -> row.getDate().equals(date)).toList();
        long present = today.stream().filter(row -> row.getStatus() == AttendanceStatus.PRESENT).count();
        long late = today.stream().filter(row -> row.getStatus() == AttendanceStatus.LATE).count();
        long absent = today.stream().filter(row -> row.getStatus() == AttendanceStatus.ABSENT).count();
        return Map.of("totalStudents", students.count(), "totalTeachers", teachers.count(),
                "totalClasses", lookupClassCount(), "presentToday", present, "lateToday", late, "absentToday", absent,
                "attendancePercentage", today.isEmpty() ? 0 : Math.round((present + late) * 10000.0 / today.size()) / 100.0,
                "recentActivity", today.stream().limit(8).map(this::response).toList());
    }

    private long lookupClassCount() { return classes.count(); }

    private Teacher authorizedTeacher(Subject subject, String username, Role role) {
        if (role == Role.ADMIN) return subject.getTeacher();
        if (role != Role.TEACHER || !subject.getTeacher().getEmail().equalsIgnoreCase(username))
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not assigned to this subject");
        return subject.getTeacher();
    }
    private AttendanceResponse response(Attendance a) {
        Student student = a.getStudent();
        SchoolClass clazz = a.getSubject().getSchoolClass();
        return new AttendanceResponse(a.getId(), student.getId(), student.getStudentCode(), student.getName(),
                clazz.getId(), clazz.getName(), a.getSubject().getId(), a.getSubject().getName(), a.getSubject().getCode(),
                a.getTeacher().getId(), a.getTeacher().getName(), a.getDate(), a.getStatus());
    }
}
