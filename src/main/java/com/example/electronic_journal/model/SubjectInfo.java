package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class SubjectInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seminarian_id")
    private Professor seminarian;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @JsonIgnore
    @OneToMany(mappedBy = "subjectInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Module> modules;

    @JsonIgnore
    @OneToMany(mappedBy = "subjectInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentPerformanceInSubject> studentsPerformancesInSubject;

    private Long lecturerId;
    @Column(nullable = false)
    private Boolean isExam;
    @Column(nullable = false)
    private Boolean isDifferentiatedCredit;

    public SubjectInfo() {
    }

    public SubjectInfo(Group group, Subject subject, Long lecturerId, Professor seminarian, Semester semester, Boolean isExam, Boolean isDifferentiatedCredit) {
        this.group = group;
        this.subject = subject;
        this.seminarian = seminarian;
        this.semester = semester;
        this.lecturerId = lecturerId;
        this.isExam = isExam;
        this.isDifferentiatedCredit = isDifferentiatedCredit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Professor getSeminarian() {
        return seminarian;
    }

    public void setSeminarian(Professor seminarian) {
        this.seminarian = seminarian;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public Set<StudentPerformanceInSubject> getStudentsPerformancesInSubject() {
        return studentsPerformancesInSubject;
    }

    public void setStudentsPerformancesInSubject(Set<StudentPerformanceInSubject> studentsPerformancesInSubject) {
        this.studentsPerformancesInSubject = studentsPerformancesInSubject;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }

    public Boolean getIsExam() {
        return isExam;
    }

    public void setIsExam(Boolean exam) {
        isExam = exam;
    }

    public Boolean getIsDifferentiatedCredit() {
        return isDifferentiatedCredit;
    }

    public void setIsDifferentiatedCredit(Boolean differentiatedCredit) {
        isDifferentiatedCredit = differentiatedCredit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectInfo that = (SubjectInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(group, that.group) && Objects.equals(subject, that.subject) && Objects.equals(seminarian, that.seminarian) && Objects.equals(semester, that.semester) && Objects.equals(lecturerId, that.lecturerId) && Objects.equals(isExam, that.isExam) && Objects.equals(isDifferentiatedCredit, that.isDifferentiatedCredit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, subject, seminarian, semester, lecturerId, isExam, isDifferentiatedCredit);
    }

    @Override
    public String toString() {
        return "SubjectInfo{" +
                "id=" + id +
                ", group=" + group +
                ", subject=" + subject +
                ", seminarian=" + seminarian +
                ", semester=" + semester +
                ", lecturerId=" + lecturerId +
                ", isExam=" + isExam +
                ", isDifferentiatedCredit=" + isDifferentiatedCredit +
                '}';
    }
}
