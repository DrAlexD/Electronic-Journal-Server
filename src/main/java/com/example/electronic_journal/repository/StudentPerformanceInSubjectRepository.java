package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.StudentPerformanceInSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentPerformanceInSubjectRepository extends JpaRepository<StudentPerformanceInSubject, Long> {
    @Query("select s from StudentPerformanceInSubject s where s.student.id= ?1 and s.subjectInfo.semester.id = ?2")
    List<StudentPerformanceInSubject> findByProfessorIdAndSemesterId(Long studentId, Long semesterId);
}
