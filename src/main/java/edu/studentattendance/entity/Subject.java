package edu.studentattendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    protected Subject() {}
    public Subject(String name, String code, SchoolClass schoolClass, Teacher teacher) {
        this.name = name; this.code = code; this.schoolClass = schoolClass; this.teacher = teacher;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public SchoolClass getSchoolClass() { return schoolClass; }
    public Teacher getTeacher() { return teacher; }
    public void update(String subjectName, String subjectCode, SchoolClass clazz, Teacher assignedTeacher) {
        name = subjectName; code = subjectCode; schoolClass = clazz; teacher = assignedTeacher;
    }
}
