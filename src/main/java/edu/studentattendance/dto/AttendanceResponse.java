package edu.studentattendance.dto;

import edu.studentattendance.entity.AttendanceStatus;
import java.time.LocalDate;

public record AttendanceResponse(Long id, Long studentId, String studentCode, String studentName,
                                 Long classId, String className, Long subjectId, String subjectName,
                                 String subjectCode, Long teacherId, String teacherName,
                                 LocalDate date, AttendanceStatus status) {}
