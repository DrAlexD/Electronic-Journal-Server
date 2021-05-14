package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    @Query("select s from Module s where s.subjectInfo.id = ?1")
    List<Module> findBySubjectInfoId(Long subjectInfoId);
}
