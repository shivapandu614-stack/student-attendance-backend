package edu.studentattendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubjectRequest(@NotBlank @Size(max = 120) String name,
                             @NotBlank @Size(max = 30) String code,
                             @NotNull Long classId,
                             @NotNull Long teacherId) {}
