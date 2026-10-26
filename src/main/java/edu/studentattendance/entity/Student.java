package edu.studentattendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String studentCode;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, unique = true, length = 160)
    private String email;
    @Column(length = 30)
    private String phone;
    @Column(nullable = false, length = 100)
    private String department;
    @Column(nullable = false)
    private Integer year;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    protected Student() {}
    public Student(String studentCode, String name, String email, String phone, String department, Integer year, SchoolClass schoolClass) {
        this.studentCode = studentCode; this.name = name; this.email = email; this.phone = phone;
        this.department = department; this.year = year; this.schoolClass = schoolClass;
    }
    public Long getId() { return id; }
    public String getStudentCode() { return studentCode; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public Integer getYear() { return year; }
    public SchoolClass getSchoolClass() { return schoolClass; }
    public void update(String code, String fullName, String mail, String telephone, String dept, Integer studyYear, SchoolClass clazz) {
        studentCode = code; name = fullName; email = mail; phone = telephone; department = dept; year = studyYear; schoolClass = clazz;
    }
}
