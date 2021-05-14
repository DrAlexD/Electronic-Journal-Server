package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class StudentPerformanceInSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_info_id", nullable = false)
    private SubjectInfo subjectInfo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @JsonIgnore
    @OneToMany(mappedBy = "studentPerformanceInSubject", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentPerformanceInModule> studentPerformanceInModules;

    private Integer earnedPoints;
    private Integer bonusPoints;
    private Boolean isHaveCreditOrAdmission;
    private Integer earnedExamPoints;
    private Integer mark;

    public StudentPerformanceInSubject() {
    }

    public StudentPerformanceInSubject(SubjectInfo subjectInfo, Student student) {
        this.subjectInfo = subjectInfo;
        this.student = student;
    }

    public StudentPerformanceInSubject(SubjectInfo subjectInfo, Student student, Integer earnedPoints, Integer bonusPoints, Boolean isHaveCreditOrAdmission, Integer earnedExamPoints, Integer mark) {
        this.subjectInfo = subjectInfo;
        this.student = student;
        this.earnedPoints = earnedPoints;
        this.bonusPoints = bonusPoints;
        this.isHaveCreditOrAdmission = isHaveCreditOrAdmission;
        this.earnedExamPoints = earnedExamPoints;
        this.mark = mark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubjectInfo getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(SubjectInfo subjectInfo) {
        this.subjectInfo = subjectInfo;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(Integer earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public Boolean getIsHaveCreditOrAdmission() {
        return isHaveCreditOrAdmission;
    }

    public void setIsHaveCreditOrAdmission(Boolean haveCreditOrAdmission) {
        isHaveCreditOrAdmission = haveCreditOrAdmission;
    }

    public Integer getEarnedExamPoints() {
        return earnedExamPoints;
    }

    public void setEarnedExamPoints(Integer earnedExamPoints) {
        this.earnedExamPoints = earnedExamPoints;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Set<StudentPerformanceInModule> getStudentPerformanceInModules() {
        return studentPerformanceInModules;
    }

    public void setStudentPerformanceInModules(Set<StudentPerformanceInModule> studentPerformanceInModules) {
        this.studentPerformanceInModules = studentPerformanceInModules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentPerformanceInSubject that = (StudentPerformanceInSubject) o;
        return Objects.equals(id, that.id) && Objects.equals(subjectInfo, that.subjectInfo) && Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectInfo, student);
    }

    @Override
    public String toString() {
        return "StudentPerformanceInSubject{" +
                "id=" + id +
                ", subjectInfo=" + subjectInfo +
                ", student=" + student +
                ", earnedPoints=" + earnedPoints +
                ", bonusPoints=" + bonusPoints +
                ", isHaveCreditOrAdmission=" + isHaveCreditOrAdmission +
                ", earnedExamPoints=" + earnedExamPoints +
                ", mark=" + mark +
                '}';
    }
}
