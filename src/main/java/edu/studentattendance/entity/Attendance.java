package edu.studentattendance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(
        name = "uk_attendance_student_subject_date", columnNames = {"student_id", "subject_id", "attendance_date"}))
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "subject_id")
    private Subject subject;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @Column(name = "attendance_date", nullable = false)
    private LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10)
    private AttendanceStatus status;

    protected Attendance() {}
    public Attendance(Student student, Subject subject, Teacher teacher, LocalDate date, AttendanceStatus status) {
        this.student = student; this.subject = subject; this.teacher = teacher; this.date = date; this.status = status;
    }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Subject getSubject() { return subject; }
    public Teacher getTeacher() { return teacher; }
    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus value) { status = value; }
}
