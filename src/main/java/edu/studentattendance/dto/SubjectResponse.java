package edu.studentattendance.dto;

public record SubjectResponse(Long id, String name, String code, Long classId, String className,
                              Long teacherId, String teacherName, String teacherEmail) {}
