package edu.studentattendance.config;

import edu.studentattendance.entity.*;
import edu.studentattendance.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoDataInitializer implements ApplicationRunner {
    private final boolean enabled;
    private final UserRepository users;
    private final StudentRepository students;
    private final TeacherRepository teachers;
    private final ClassRepository classes;
    private final SubjectRepository subjects;
    private final PasswordEncoder encoder;

    public DemoDataInitializer(@Value("${app.seed.enabled}") boolean enabled, UserRepository users,
            StudentRepository students, TeacherRepository teachers, ClassRepository classes,
            SubjectRepository subjects, PasswordEncoder encoder) {
        this.enabled = enabled; this.users = users; this.students = students; this.teachers = teachers;
        this.classes = classes; this.subjects = subjects; this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) return;
        users.findByUsernameIgnoreCase("admin").orElseGet(() ->
                users.save(new AppUser("admin", encoder.encode("Admin123!"), Role.ADMIN)));
        SchoolClass clazz = classes.findAll().stream().findFirst().orElseGet(() ->
                classes.save(new SchoolClass("Computer Science - Year 1", "Computer Science", "2026-2027")));
        Teacher teacher = teachers.findByEmailIgnoreCase("teacher@example.com").orElseGet(() -> {
            Teacher created = teachers.save(new Teacher("T-1001", "Alex Morgan", "teacher@example.com", "Computer Science"));
            users.findByUsernameIgnoreCase(created.getEmail()).orElseGet(() ->
                    users.save(new AppUser(created.getEmail(), encoder.encode("Teacher123!"), Role.TEACHER)));
            return created;
        });
        students.findByStudentCodeIgnoreCase("STU-1001").orElseGet(() -> {
            Student created = students.save(new Student("STU-1001", "Jordan Lee", "student@example.com",
                    "555-0100", "Computer Science", 1, clazz));
            users.findByUsernameIgnoreCase(created.getStudentCode()).orElseGet(() ->
                    users.save(new AppUser(created.getStudentCode(), encoder.encode("Student123!"), Role.STUDENT)));
            return created;
        });
        if (subjects.count() == 0)
            subjects.save(new Subject("Introduction to Computing", "CS101", clazz, teacher));
    }
}
