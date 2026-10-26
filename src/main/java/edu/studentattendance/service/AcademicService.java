package edu.studentattendance.service;

import edu.studentattendance.dto.*;
import edu.studentattendance.entity.*;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.ClassRepository;
import edu.studentattendance.repository.SubjectRepository;
import edu.studentattendance.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AcademicService {
    private final ClassRepository classes;
    private final SubjectRepository subjects;
    private final LookupService lookup;
    private final StudentRepository students;
    public AcademicService(ClassRepository classes, SubjectRepository subjects, LookupService lookup, StudentRepository students) {
        this.classes = classes; this.subjects = subjects; this.lookup = lookup; this.students = students;
    }
    @Transactional(readOnly = true)
    public List<ClassResponse> classes(String username, Role role) {
        List<SchoolClass> result;
        if (role == Role.TEACHER) {
            List<Long> ids = subjects.findByTeacher_EmailIgnoreCase(username).stream()
                    .map(subject -> subject.getSchoolClass().getId()).distinct().toList();
            result = ids.isEmpty() ? List.of() : classes.findAllById(ids);
        } else if (role == Role.STUDENT) {
            Student student = students.findByStudentCodeIgnoreCase(username)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student profile not found"));
            result = student.getSchoolClass() == null ? List.of() : List.of(student.getSchoolClass());
        } else result = classes.findAll();
        return result.stream().map(c -> new ClassResponse(c.getId(), c.getName(), c.getDepartment(), c.getAcademicYear())).toList();
    }
    public ClassResponse createClass(ClassRequest request) {
        return classResponse(classes.save(new SchoolClass(request.name().trim(), request.department().trim(), request.academicYear().trim())));
    }
    @Transactional
    public ClassResponse updateClass(Long id, ClassRequest request) {
        SchoolClass c = lookup.schoolClass(id);
        c.setName(request.name().trim()); c.setDepartment(request.department().trim()); c.setAcademicYear(request.academicYear().trim());
        return classResponse(classes.save(c));
    }
    public void deleteClass(Long id) { classes.delete(lookup.schoolClass(id)); }
    @Transactional(readOnly = true)
    public List<SubjectResponse> subjects(String username, Role role) {
        List<Subject> result;
        if (role == Role.TEACHER) result = subjects.findByTeacher_EmailIgnoreCase(username);
        else if (role == Role.STUDENT) {
            Student student = students.findByStudentCodeIgnoreCase(username)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student profile not found"));
            result = subjects.findAll().stream().filter(subject -> student.getSchoolClass() != null
                    && subject.getSchoolClass().getId().equals(student.getSchoolClass().getId())).toList();
        } else result = subjects.findAll();
        return result.stream().map(this::subjectResponse).toList();
    }
    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjects.existsByCodeIgnoreCase(request.code()))
            throw new ApiException(HttpStatus.CONFLICT, "Subject code already exists");
        return subjectResponse(subjects.save(new Subject(request.name().trim(), request.code().trim(),
                lookup.schoolClass(request.classId()), lookup.teacher(request.teacherId()))));
    }
    @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = lookup.subject(id);
        if (!subject.getCode().equalsIgnoreCase(request.code()) && subjects.existsByCodeIgnoreCase(request.code()))
            throw new ApiException(HttpStatus.CONFLICT, "Subject code already exists");
        subject.update(request.name().trim(), request.code().trim(), lookup.schoolClass(request.classId()), lookup.teacher(request.teacherId()));
        return subjectResponse(subjects.save(subject));
    }
    public void deleteSubject(Long id) { subjects.delete(lookup.subject(id)); }
    private ClassResponse classResponse(SchoolClass c) {
        return new ClassResponse(c.getId(), c.getName(), c.getDepartment(), c.getAcademicYear());
    }
    private SubjectResponse subjectResponse(Subject s) {
        return new SubjectResponse(s.getId(), s.getName(), s.getCode(), s.getSchoolClass().getId(),
                s.getSchoolClass().getName(), s.getTeacher().getId(), s.getTeacher().getName(), s.getTeacher().getEmail());
    }
}
