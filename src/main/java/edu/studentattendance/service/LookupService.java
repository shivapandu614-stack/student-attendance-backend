package edu.studentattendance.service;

import edu.studentattendance.entity.*;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LookupService {
    private final ClassRepository classes;
    private final TeacherRepository teachers;
    private final StudentRepository students;
    private final SubjectRepository subjects;

    public LookupService(ClassRepository classes, TeacherRepository teachers, StudentRepository students, SubjectRepository subjects) {
        this.classes = classes; this.teachers = teachers; this.students = students; this.subjects = subjects;
    }
    public SchoolClass schoolClass(Long id) {
        return classes.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Class not found"));
    }
    public Teacher teacher(Long id) {
        return teachers.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Teacher not found"));
    }
    public Student student(Long id) {
        return students.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found"));
    }
    public Subject subject(Long id) {
        return subjects.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Subject not found"));
    }
}
