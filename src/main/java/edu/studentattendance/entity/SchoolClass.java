package edu.studentattendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "classes")
public class SchoolClass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 100)
    private String department;
    @Column(nullable = false, length = 30)
    private String academicYear;

    protected SchoolClass() {}
    public SchoolClass(String name, String department, String academicYear) {
        this.name = name; this.department = department; this.academicYear = academicYear;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getAcademicYear() { return academicYear; }
    public void setName(String value) { name = value; }
    public void setDepartment(String value) { department = value; }
    public void setAcademicYear(String value) { academicYear = value; }
}
