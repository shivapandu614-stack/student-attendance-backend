package edu.studentattendance.repository;

import edu.studentattendance.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentCodeIgnoreCase(String studentCode);
    Optional<Student> findByEmailIgnoreCase(String email);
    boolean existsByStudentCodeIgnoreCase(String code);
    boolean existsByEmailIgnoreCase(String email);
    List<Student> findBySchoolClass_IdIn(List<Long> classIds);
}
