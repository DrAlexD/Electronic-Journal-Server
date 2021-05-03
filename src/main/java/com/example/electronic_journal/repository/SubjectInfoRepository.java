package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.SubjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectInfoRepository extends JpaRepository<SubjectInfo, Long> {
    @Query("select s from SubjectInfo s where s.lecturerId= ?1 and s.semester.id = ?2 or " +
            "s.seminarian.id= ?1 and s.semester.id = ?2")
    List<SubjectInfo> findByProfessorIdAndSemesterId(Long professorId, Long semesterId);

    @Query("select s from SubjectInfo s where s.lecturerId= ?1 and s.semester.id = ?2 and s.subject.id = ?3  or " +
            "s.seminarian.id= ?1 and s.semester.id = ?2 and s.subject.id = ?3")
    List<SubjectInfo> findByProfessorIdAndSemesterIdAndSubjectId(Long professorId, Long semesterId, Long subjectId);
}
