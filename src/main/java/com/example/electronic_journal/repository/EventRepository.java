package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select s from Event s where s.module.subjectInfo.id = ?1")
    List<Event> findBySubjectInfoId(Long subjectInfoId);

    @Query("select s from Event s where s.module.subjectInfo.id = ?1 and  s.type = ?2")
    List<Event> findBySubjectInfoIdAndType(Long subjectInfoId, Integer type);
}
