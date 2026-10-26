package edu.studentattendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClassRequest(@NotBlank @Size(max = 100) String name,
                           @NotBlank @Size(max = 100) String department,
                           @NotBlank @Size(max = 30) String academicYear) {}
