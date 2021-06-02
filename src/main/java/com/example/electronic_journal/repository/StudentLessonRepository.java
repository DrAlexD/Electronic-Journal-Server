package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.StudentLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentLessonRepository extends JpaRepository<StudentLesson, Long> {
    @Query("select s from StudentLesson s where s.lesson.module.subjectInfo.id = ?1")
    List<StudentLesson> findBySubjectInfoId(Long subjectInfoId);

    @Query("select s from StudentLesson s where s.studentPerformanceInModule.studentPerformanceInSubject.id = ?1")
    List<StudentLesson> findByStudentPerformanceInSubjectId(Long studentPerformanceInSubjectId);

    @Query("select s from StudentLesson s where s.lesson.id = ?1")
    List<StudentLesson> findByLessonId(Long lessonId);
}
