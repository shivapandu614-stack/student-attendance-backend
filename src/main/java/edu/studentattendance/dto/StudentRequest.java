package edu.studentattendance.dto;

import jakarta.validation.constraints.*;

public record StudentRequest(
        @NotBlank @Size(max = 40) String studentCode,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @Size(max = 30) String phone,
        @NotBlank @Size(max = 100) String department,
        @NotNull @Min(1) @Max(8) Integer year,
        Long classId,
        String password) {}
