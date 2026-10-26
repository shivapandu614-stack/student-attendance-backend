package edu.studentattendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String teacherCode;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, unique = true, length = 160)
    private String email;
    @Column(nullable = false, length = 100)
    private String department;

    protected Teacher() {}
    public Teacher(String teacherCode, String name, String email, String department) {
        this.teacherCode = teacherCode; this.name = name; this.email = email; this.department = department;
    }
    public Long getId() { return id; }
    public String getTeacherCode() { return teacherCode; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public void update(String code, String fullName, String mail, String dept) {
        teacherCode = code; name = fullName; email = mail; department = dept;
    }
}
