package edu.studentattendance.dto;

import edu.studentattendance.entity.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record AttendanceBatchRequest(@NotNull Long subjectId, @NotNull LocalDate date,
                                     @NotEmpty List<@Valid Entry> entries) {
    public record Entry(@NotNull Long studentId, @NotNull AttendanceStatus status) {}
}
