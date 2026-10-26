package edu.studentattendance.repository;

import edu.studentattendance.entity.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByStudent_IdAndSubject_IdAndDate(Long studentId, Long subjectId, LocalDate date);
    @EntityGraph(attributePaths = {"student", "student.schoolClass", "subject", "subject.schoolClass", "teacher"})
    List<Attendance> findAllByOrderByDateDesc();
    @EntityGraph(attributePaths = {"student", "student.schoolClass", "subject", "subject.schoolClass", "teacher"})
    List<Attendance> findByStudent_StudentCodeIgnoreCaseOrderByDateDesc(String studentCode);
}
