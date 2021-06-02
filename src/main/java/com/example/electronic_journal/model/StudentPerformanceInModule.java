package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class StudentPerformanceInModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_performance_in_subject_id", nullable = false)
    private StudentPerformanceInSubject studentPerformanceInSubject;

    @JsonIgnore
    @OneToMany(mappedBy = "studentPerformanceInModule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentLesson> studentLessons;

    @JsonIgnore
    @OneToMany(mappedBy = "studentPerformanceInModule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentEvent> studentEvents;

    private Integer earnedPoints;
    private Boolean isHasCredit;

    public StudentPerformanceInModule() {
    }

    public StudentPerformanceInModule(Module module, StudentPerformanceInSubject studentPerformanceInSubject) {
        this.module = module;
        this.studentPerformanceInSubject = studentPerformanceInSubject;
    }

    public StudentPerformanceInModule(Module module, StudentPerformanceInSubject studentPerformanceInSubject, Integer earnedPoints, Boolean isHasCredit) {
        this.module = module;
        this.studentPerformanceInSubject = studentPerformanceInSubject;
        this.earnedPoints = earnedPoints;
        this.isHasCredit = isHasCredit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public StudentPerformanceInSubject getStudentPerformanceInSubject() {
        return studentPerformanceInSubject;
    }

    public void setStudentPerformanceInSubject(StudentPerformanceInSubject studentPerformanceInSubject) {
        this.studentPerformanceInSubject = studentPerformanceInSubject;
    }

    public Set<StudentLesson> getStudentLessons() {
        return studentLessons;
    }

    public void setStudentLessons(Set<StudentLesson> studentLessons) {
        this.studentLessons = studentLessons;
    }

    public Set<StudentEvent> getStudentEvents() {
        return studentEvents;
    }

    public void setStudentEvents(Set<StudentEvent> studentEvents) {
        this.studentEvents = studentEvents;
    }

    public Integer getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(Integer earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public Boolean getIsHasCredit() {
        return isHasCredit;
    }

    public void setIsHasCredit(Boolean haveCredit) {
        isHasCredit = haveCredit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentPerformanceInModule that = (StudentPerformanceInModule) o;
        return Objects.equals(id, that.id) && Objects.equals(module, that.module) && Objects.equals(studentPerformanceInSubject, that.studentPerformanceInSubject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, module, studentPerformanceInSubject);
    }

    @Override
    public String toString() {
        return "StudentPerformanceInModule{" +
                "id=" + id +
                ", module=" + module +
                ", studentPerformanceInSubject=" + studentPerformanceInSubject +
                ", earnedPoints=" + earnedPoints +
                ", isHaveCredit=" + isHasCredit +
                '}';
    }
}
