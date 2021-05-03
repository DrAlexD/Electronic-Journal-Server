package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.StudentLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentLessonRepository extends JpaRepository<StudentLesson, Long> {
}
