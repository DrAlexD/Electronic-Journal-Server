package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class StudentEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_performance_in_module_id", nullable = false)
    private StudentPerformanceInModule studentPerformanceInModule;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Integer attemptNumber;
    @Column(nullable = false)
    private Boolean isAttended;
    @Column(nullable = false)
    private Integer variantNumber;

    @Temporal(TemporalType.DATE)
    private Date finishDate;
    private Integer earnedPoints;
    private Integer bonusPoints;
    private Boolean isHaveCredit;

    public StudentEvent() {
    }

    public StudentEvent(Integer attemptNumber, StudentPerformanceInModule studentPerformanceInModule, Event event, Boolean isAttended, Integer variantNumber) {
        this.studentPerformanceInModule = studentPerformanceInModule;
        this.event = event;
        this.attemptNumber = attemptNumber;
        this.isAttended = isAttended;
        this.variantNumber = variantNumber;
    }

    public StudentEvent(Integer attemptNumber, StudentPerformanceInModule studentPerformanceInModule, Event event, Boolean isAttended, Integer variantNumber, Date finishDate, Integer earnedPoints, Integer bonusPoints, Boolean isHaveCredit) {
        this.studentPerformanceInModule = studentPerformanceInModule;
        this.event = event;
        this.attemptNumber = attemptNumber;
        this.isAttended = isAttended;
        this.variantNumber = variantNumber;
        this.finishDate = finishDate;
        this.earnedPoints = earnedPoints;
        this.bonusPoints = bonusPoints;
        this.isHaveCredit = isHaveCredit;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Boolean getAttended() {
        return isAttended;
    }

    public void setAttended(Boolean attended) {
        isAttended = attended;
    }

    public Integer getVariantNumber() {
        return variantNumber;
    }

    public void setVariantNumber(Integer variantNumber) {
        this.variantNumber = variantNumber;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
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

    public Boolean getHaveCredit() {
        return isHaveCredit;
    }

    public void setHaveCredit(Boolean haveCredit) {
        isHaveCredit = haveCredit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentEvent that = (StudentEvent) o;
        return Objects.equals(id, that.id) && Objects.equals(studentPerformanceInModule, that.studentPerformanceInModule) && Objects.equals(event, that.event) && Objects.equals(attemptNumber, that.attemptNumber) && Objects.equals(isAttended, that.isAttended) && Objects.equals(variantNumber, that.variantNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentPerformanceInModule, event, attemptNumber, isAttended, variantNumber);
    }

    @Override
    public String toString() {
        return "StudentEvent{" +
                "id=" + id +
                ", studentPerformanceInModule=" + studentPerformanceInModule +
                ", event=" + event +
                ", attemptNumber=" + attemptNumber +
                ", isAttended=" + isAttended +
                ", variantNumber=" + variantNumber +
                ", finishDate=" + finishDate +
                ", earnedPoints=" + earnedPoints +
                ", bonusPoints=" + bonusPoints +
                ", isHaveCredit=" + isHaveCredit +
                '}';
    }
}
