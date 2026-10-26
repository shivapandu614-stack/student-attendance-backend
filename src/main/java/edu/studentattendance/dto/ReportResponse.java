package edu.studentattendance.dto;

public record ReportResponse(String studentCode, String studentName, String className, long totalClasses,
                             long attended, long absent, long late, double percentage) {}
