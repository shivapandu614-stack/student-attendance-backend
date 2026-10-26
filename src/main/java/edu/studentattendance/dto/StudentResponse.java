package edu.studentattendance.dto;

public record StudentResponse(Long id, String studentCode, String name, String email, String phone,
                              String department, Integer year, Long classId, String className) {}
