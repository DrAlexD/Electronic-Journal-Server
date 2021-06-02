package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.StudentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentEventRepository extends JpaRepository<StudentEvent, Long> {
    @Query("select s from StudentEvent s where s.event.module.subjectInfo.id = ?1")
    List<StudentEvent> findBySubjectInfoId(Long subjectInfoId);

    @Query("select s from StudentEvent s where s.studentPerformanceInModule.studentPerformanceInSubject.id = ?1")
    List<StudentEvent> findByStudentPerformanceInSubjectId(Long studentPerformanceInSubjectId);

    @Query("select s from StudentEvent s where s.studentPerformanceInModule.id = ?1 and s.event.id = ?2")
    List<StudentEvent> findByStudentPerformanceInModuleIdAndEventId(Long studentPerformanceInModuleId, Long eventId);

    @Query("select s from StudentEvent s where s.event.id = ?1")
    List<StudentEvent> findByEventId(Long eventId);
}
