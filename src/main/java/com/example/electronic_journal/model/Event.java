package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @JsonBackReference
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<StudentEvent> studentsEvent;

    @Column(nullable = false)
    private Integer type;
    @Column(nullable = false)
    private Integer number;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date deadlineDate;
    @Column(nullable = false)
    private Integer minPoints;
    @Column(nullable = false)
    private Integer maxPoints;


    public Event() {
    }

    public Event(Module module, Integer type, Integer number, Date startDate, Date deadlineDate, Integer minPoints, Integer maxPoints) {
        this.module = module;
        this.type = type;
        this.number = number;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Set<StudentEvent> getStudentsEvent() {
        return studentsEvent;
    }

    public void setStudentsEvent(Set<StudentEvent> studentsEvent) {
        this.studentsEvent = studentsEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(module, event.module) && Objects.equals(type, event.type) && Objects.equals(number, event.number) && Objects.equals(startDate, event.startDate) && Objects.equals(deadlineDate, event.deadlineDate) && Objects.equals(minPoints, event.minPoints) && Objects.equals(maxPoints, event.maxPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, module, type, number, startDate, deadlineDate, minPoints, maxPoints);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", module=" + module +
                ", type=" + type +
                ", number=" + number +
                ", startDate=" + startDate +
                ", deadlineDate=" + deadlineDate +
                ", minPoints=" + minPoints +
                ", maxPoints=" + maxPoints +
                '}';
    }
}
