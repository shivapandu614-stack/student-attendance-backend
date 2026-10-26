package edu.studentattendance.repository;

import edu.studentattendance.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByTeacher_EmailIgnoreCase(String email);
    boolean existsByCodeIgnoreCase(String code);
}
