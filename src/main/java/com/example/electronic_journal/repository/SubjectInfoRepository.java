package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.SubjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectInfoRepository extends JpaRepository<SubjectInfo, Long> {
    @Query("select s from SubjectInfo s where s.lecturerId= ?1 and s.semester.id = ?2 or " +
            "s.seminarsProfessor.id= ?1 and s.semester.id = ?2")
    List<SubjectInfo> findByProfessorIdAndSemesterId(Long professorId, Long semesterId);

    @Query("select s from SubjectInfo s where s.lecturerId= ?1 and s.semester.id = ?2 and s.subject.id = ?3  or " +
            "s.seminarsProfessor.id= ?1 and s.semester.id = ?2 and s.subject.id = ?3")
    List<SubjectInfo> findByProfessorIdAndSemesterIdAndSubjectId(Long professorId, Long semesterId, Long subjectId);

    @Query("select s from SubjectInfo s where s.group.id= ?1 and s.semester.id = ?2 and s.subject.id = ?3 ")
    Optional<SubjectInfo> findByGroupIdAndSemesterIdAndSubjectId(Long groupId, Long semesterId, Long subjectId);

    @Query("select s from SubjectInfo s where s.group.id= ?1")
    List<SubjectInfo> findByGroupId(Long groupId);
}
