package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.StudentPerformanceInModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentPerformanceInModuleRepository extends JpaRepository<StudentPerformanceInModule, Long> {
    @Query("select s from StudentPerformanceInModule s where s.module.subjectInfo.id = ?1")
    List<StudentPerformanceInModule> findBySubjectInfoId(Long subjectInfoId);

    @Query("select s from StudentPerformanceInModule s where s.studentPerformanceInSubject.id = ?1")
    List<StudentPerformanceInModule> findByStudentPerformanceInSubjectId(Long studentPerformanceInSubjectId);
}
