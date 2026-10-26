package edu.studentattendance.repository;

import edu.studentattendance.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<SchoolClass, Long> {}
