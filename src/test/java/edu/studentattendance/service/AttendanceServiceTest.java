package edu.studentattendance.service;

import edu.studentattendance.dto.AttendanceBatchRequest;
import edu.studentattendance.entity.*;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {
    private AttendanceRepository attendance;
    private LookupService lookup;
    private AttendanceService service;
    private SchoolClass schoolClass;
    private Teacher teacher;
    private Subject subject;

    @BeforeEach
    void setUp() {
        attendance = mock(AttendanceRepository.class);
        lookup = mock(LookupService.class);
        service = new AttendanceService(attendance, mock(StudentRepository.class), mock(TeacherRepository.class),
                mock(ClassRepository.class), lookup);
        schoolClass = new SchoolClass("Year 1", "Computing", "2026-2027");
        teacher = new Teacher("T-1", "Taylor Teacher", "teacher@example.com", "Computing");
        subject = new Subject("Programming", "CS101", schoolClass, teacher);
    }

    @Test
    void reportsCountLateAsAttendedAndRoundToTwoDecimalPlaces() {
        Student student = new Student("S-1", "Sam Student", "sam@example.com", null, "Computing", 1, schoolClass);
        List<Attendance> records = List.of(
                new Attendance(student, subject, teacher, LocalDate.of(2026, 1, 1), AttendanceStatus.PRESENT),
                new Attendance(student, subject, teacher, LocalDate.of(2026, 1, 2), AttendanceStatus.LATE),
                new Attendance(student, subject, teacher, LocalDate.of(2026, 1, 3), AttendanceStatus.ABSENT));
        when(attendance.findAllByOrderByDateDesc()).thenReturn(records);

        var report = service.reports(null, null, null, null, null, "admin", Role.ADMIN).get(0);

        assertEquals(3, report.totalClasses());
        assertEquals(2, report.attended());
        assertEquals(1, report.absent());
        assertEquals(1, report.late());
        assertEquals(66.67, report.percentage());
    }

    @Test
    void rejectsAttendanceFromTeacherNotAssignedToSubject() {
        when(lookup.subject(1L)).thenReturn(subject);
        var request = new AttendanceBatchRequest(1L, LocalDate.now(),
                List.of(new AttendanceBatchRequest.Entry(2L, AttendanceStatus.PRESENT)));

        ApiException exception = assertThrows(ApiException.class,
                () -> service.mark(request, "other@example.com", Role.TEACHER));

        assertEquals(403, exception.getStatus().value());
        verify(attendance, never()).save(any());
    }

    @Test
    void rejectsStudentRepeatedWithinAttendanceBatch() {
        when(lookup.subject(1L)).thenReturn(subject);
        Student student = new Student("S-1", "Sam Student", "sam@example.com", null, "Computing", 1, schoolClass);
        when(lookup.student(2L)).thenReturn(student);
        var entry = new AttendanceBatchRequest.Entry(2L, AttendanceStatus.PRESENT);
        var request = new AttendanceBatchRequest(1L, LocalDate.now(), List.of(entry, entry));

        ApiException exception = assertThrows(ApiException.class,
                () -> service.mark(request, teacher.getEmail(), Role.TEACHER));

        assertEquals(400, exception.getStatus().value());
        verify(attendance, never()).save(any());
    }
}
