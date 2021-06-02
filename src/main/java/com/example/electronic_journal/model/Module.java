package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_info_id", nullable = false)
    private SubjectInfo subjectInfo;

    @JsonIgnore
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Lesson> lessons;

    @JsonIgnore
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Event> events;

    @JsonIgnore
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentPerformanceInModule> studentsPerformancesInModule;

    @Column(nullable = false)
    private Integer moduleNumber;
    @Column(nullable = false)
    private Integer minPoints;
    @Column(nullable = false)
    private Integer maxAvailablePoints;

    public Module() {
    }

    public Module(Integer moduleNumber, SubjectInfo subjectInfo, Integer minPoints, Integer maxAvailablePoints) {
        this.moduleNumber = moduleNumber;
        this.subjectInfo = subjectInfo;
        this.minPoints = minPoints;
        this.maxAvailablePoints = maxAvailablePoints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getModuleNumber() {
        return moduleNumber;
    }

    public void setModuleNumber(Integer moduleNumber) {
        this.moduleNumber = moduleNumber;
    }

    public SubjectInfo getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(SubjectInfo subjectInfo) {
        this.subjectInfo = subjectInfo;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxAvailablePoints() {
        return maxAvailablePoints;
    }

    public void setMaxAvailablePoints(Integer maxAvailablePoints) {
        this.maxAvailablePoints = maxAvailablePoints;
    }

    public Set<StudentPerformanceInModule> getStudentsPerformancesInModule() {
        return studentsPerformancesInModule;
    }

    public void setStudentsPerformancesInModule(Set<StudentPerformanceInModule> studentsPerformancesInModule) {
        this.studentsPerformancesInModule = studentsPerformancesInModule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(id, module.id) && Objects.equals(subjectInfo, module.subjectInfo) && Objects.equals(moduleNumber, module.moduleNumber) && Objects.equals(minPoints, module.minPoints) && Objects.equals(maxAvailablePoints, module.maxAvailablePoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subjectInfo, moduleNumber, minPoints, maxAvailablePoints);
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", subjectInfo=" + subjectInfo +
                ", moduleNumber=" + moduleNumber +
                ", minPoints=" + minPoints +
                ", maxPoints=" + maxAvailablePoints +
                '}';
    }
}
