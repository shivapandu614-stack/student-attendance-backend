package edu.studentattendance.service;

import edu.studentattendance.dto.StudentRequest;
import edu.studentattendance.dto.StudentResponse;
import edu.studentattendance.entity.*;
import edu.studentattendance.exception.ApiException;
import edu.studentattendance.repository.StudentRepository;
import edu.studentattendance.repository.SubjectRepository;
import edu.studentattendance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository students;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final LookupService lookup;
    private final PasswordEncoder encoder;
    public StudentService(StudentRepository students, UserRepository users, SubjectRepository subjects, LookupService lookup, PasswordEncoder encoder) {
        this.students = students; this.users = users; this.subjects = subjects; this.lookup = lookup; this.encoder = encoder;
    }
    @Transactional(readOnly = true)
    public List<StudentResponse> list() { return students.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public List<StudentResponse> list(String username, Role role) {
        if (role == Role.ADMIN) return list();
        List<Long> classIds = subjects.findByTeacher_EmailIgnoreCase(username).stream()
                .map(subject -> subject.getSchoolClass().getId()).distinct().toList();
        return classIds.isEmpty() ? List.of() : students.findBySchoolClass_IdIn(classIds).stream().map(this::response).toList();
    }
    @Transactional(readOnly = true)
    public StudentResponse get(Long id) { return response(lookup.student(id)); }
    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (request.password() == null || request.password().length() < 8)
            throw new ApiException(HttpStatus.BAD_REQUEST, "A password of at least 8 characters is required");
        if (students.existsByStudentCodeIgnoreCase(request.studentCode()) || students.existsByEmailIgnoreCase(request.email())
                || users.existsByUsernameIgnoreCase(request.studentCode()))
            throw new ApiException(HttpStatus.CONFLICT, "Student code or email already exists");
        Student student = students.save(new Student(request.studentCode().trim(), request.name().trim(),
                request.email().trim().toLowerCase(), request.phone(), request.department().trim(),
                request.year(), request.classId() == null ? null : lookup.schoolClass(request.classId())));
        users.save(new AppUser(student.getStudentCode(), encoder.encode(request.password()), Role.STUDENT));
        return response(student);
    }
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = lookup.student(id);
        if (!student.getStudentCode().equalsIgnoreCase(request.studentCode())
                && (students.existsByStudentCodeIgnoreCase(request.studentCode()) || users.existsByUsernameIgnoreCase(request.studentCode())))
            throw new ApiException(HttpStatus.CONFLICT, "Student code already exists");
        if (!student.getEmail().equalsIgnoreCase(request.email()) && students.existsByEmailIgnoreCase(request.email()))
            throw new ApiException(HttpStatus.CONFLICT, "Student email already exists");
        SchoolClass clazz = request.classId() == null ? null : lookup.schoolClass(request.classId());
        String oldCode = student.getStudentCode();
        student.update(request.studentCode().trim(), request.name().trim(), request.email().trim().toLowerCase(),
                request.phone(), request.department().trim(), request.year(), clazz);
        users.findByUsernameIgnoreCase(oldCode).ifPresent(user -> {
            user.setUsername(request.studentCode().trim());
            users.save(user);
        });
        return response(students.save(student));
    }
    @Transactional
    public void delete(Long id) {
        Student student = lookup.student(id);
        users.findByUsernameIgnoreCase(student.getStudentCode()).ifPresent(users::delete);
        students.delete(student);
    }
    private StudentResponse response(Student student) {
        SchoolClass clazz = student.getSchoolClass();
        return new StudentResponse(student.getId(), student.getStudentCode(), student.getName(), student.getEmail(),
                student.getPhone(), student.getDepartment(), student.getYear(), clazz == null ? null : clazz.getId(),
                clazz == null ? null : clazz.getName());
    }
}
