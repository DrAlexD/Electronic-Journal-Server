package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.StudentPerformanceInModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPerformanceInModuleRepository extends JpaRepository<StudentPerformanceInModule, Long> {
}
