CREATE DATABASE IF NOT EXISTS student_attendance_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_attendance_db;

-- Reference schema. Hibernate ddl-auto=update creates/updates these tables at startup.
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username)
);
CREATE TABLE IF NOT EXISTS classes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    academic_year VARCHAR(30) NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    teacher_code VARCHAR(40) NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    department VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_teachers_teacher_code (teacher_code),
    UNIQUE KEY uk_teachers_email (email)
);
CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_code VARCHAR(40) NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    phone VARCHAR(30),
    department VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    class_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_students_student_code (student_code),
    UNIQUE KEY uk_students_email (email),
    CONSTRAINT fk_students_class FOREIGN KEY (class_id) REFERENCES classes (id)
);
CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(30) NOT NULL,
    class_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_subjects_code (code),
    CONSTRAINT fk_subjects_class FOREIGN KEY (class_id) REFERENCES classes (id),
    CONSTRAINT fk_subjects_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_attendance_student_subject_date (student_id, subject_id, attendance_date),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_attendance_subject FOREIGN KEY (subject_id) REFERENCES subjects (id),
    CONSTRAINT fk_attendance_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);
