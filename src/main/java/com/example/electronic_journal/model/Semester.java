package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer year;
    @Column(nullable = false)
    private Boolean isFirstHalf;

    @JsonIgnore
    @OneToMany(mappedBy = "semester", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SubjectInfo> subjectInfos;

    public Semester() {
    }

    public Semester(Integer year, Boolean isFirstHalf) {
        super();
        this.year = year;
        this.isFirstHalf = isFirstHalf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean getIsFirstHalf() {
        return isFirstHalf;
    }

    public void setIsFirstHalf(Boolean firstHalf) {
        isFirstHalf = firstHalf;
    }

    public Set<SubjectInfo> getSubjectInfos() {
        return subjectInfos;
    }

    public void setSubjectInfos(Set<SubjectInfo> subjectInfos) {
        this.subjectInfos = subjectInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Semester semester = (Semester) o;
        return Objects.equals(id, semester.id) && Objects.equals(year, semester.year) && Objects.equals(isFirstHalf, semester.isFirstHalf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, isFirstHalf);
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", year=" + year +
                ", isFirstHalf=" + isFirstHalf +
                '}';
    }
}
