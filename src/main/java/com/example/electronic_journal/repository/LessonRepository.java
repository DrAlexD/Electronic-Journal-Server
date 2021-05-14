package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("select s from Lesson s where s.module.subjectInfo.id = ?1")
    List<Lesson> findBySubjectInfoId(Long subjectInfoId);

    @Query("select s from Lesson s where s.module.subjectInfo.id= ?1 and s.isLecture= ?2")
    List<Lesson> findBySubjectInfoIdAndIsLecture(Long subjectInfoId, Boolean isLecture);
}
