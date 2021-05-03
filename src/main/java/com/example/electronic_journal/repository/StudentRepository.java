package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGroupId(Long groupId);

    Optional<Student> findByUsername(String username);

    Boolean existsByUsername(String username);
}
