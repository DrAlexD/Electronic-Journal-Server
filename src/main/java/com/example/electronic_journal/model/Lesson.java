package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateAndTime;
    @Column(nullable = false)
    private Boolean isLecture;
    @Column(nullable = false)
    private Integer pointsPerVisit;

    @JsonIgnore
    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentLesson> studentsLesson;

    public Lesson() {
    }

    public Lesson(Module module, Date dateAndTime, Boolean isLecture, Integer pointsPerVisit) {
        this.module = module;
        this.dateAndTime = dateAndTime;
        this.isLecture = isLecture;
        this.pointsPerVisit = pointsPerVisit;
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

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Date dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Boolean getIsLecture() {
        return isLecture;
    }

    public void setIsLecture(Boolean lecture) {
        isLecture = lecture;
    }

    public Integer getPointsPerVisit() {
        return pointsPerVisit;
    }

    public void setPointsPerVisit(Integer pointsPerVisit) {
        this.pointsPerVisit = pointsPerVisit;
    }

    public Set<StudentLesson> getStudentsLesson() {
        return studentsLesson;
    }

    public void setStudentsLesson(Set<StudentLesson> studentsLesson) {
        this.studentsLesson = studentsLesson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id) && Objects.equals(module, lesson.module) && Objects.equals(dateAndTime, lesson.dateAndTime) && Objects.equals(isLecture, lesson.isLecture) && Objects.equals(pointsPerVisit, lesson.pointsPerVisit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, module, dateAndTime, isLecture, pointsPerVisit);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", module=" + module +
                ", dateAndTime=" + dateAndTime +
                ", isLecture=" + isLecture +
                ", pointsPerVisit=" + pointsPerVisit +
                '}';
    }
}
