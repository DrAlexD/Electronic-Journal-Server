package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class StudentLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_performance_in_module_id", nullable = false)
    private StudentPerformanceInModule studentPerformanceInModule;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    private Boolean isAttended;
    private Integer bonusPoints;

    public StudentLesson() {
    }

    public StudentLesson(StudentPerformanceInModule studentPerformanceInModule, Lesson lesson, Boolean isAttended) {
        this.studentPerformanceInModule = studentPerformanceInModule;
        this.lesson = lesson;
        this.isAttended = isAttended;
    }

    public StudentLesson(StudentPerformanceInModule studentPerformanceInModule, Lesson lesson, Boolean isAttended, Integer bonusPoints) {
        this.studentPerformanceInModule = studentPerformanceInModule;
        this.lesson = lesson;
        this.isAttended = isAttended;
        this.bonusPoints = bonusPoints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentPerformanceInModule getStudentPerformanceInModule() {
        return studentPerformanceInModule;
    }

    public void setStudentPerformanceInModule(StudentPerformanceInModule studentPerformanceInModule) {
        this.studentPerformanceInModule = studentPerformanceInModule;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Boolean getAttended() {
        return isAttended;
    }

    public void setAttended(Boolean attended) {
        isAttended = attended;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentLesson that = (StudentLesson) o;
        return Objects.equals(id, that.id) && Objects.equals(studentPerformanceInModule, that.studentPerformanceInModule) && Objects.equals(lesson, that.lesson) && Objects.equals(isAttended, that.isAttended);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentPerformanceInModule, lesson, isAttended);
    }

    @Override
    public String toString() {
        return "StudentLesson{" +
                "id=" + id +
                ", studentPerformanceInModule=" + studentPerformanceInModule +
                ", lesson=" + lesson +
                ", isAttended=" + isAttended +
                ", bonusPoints=" + bonusPoints +
                '}';
    }
}

