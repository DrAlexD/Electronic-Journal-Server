package com.example.electronic_journal.repository;

import com.example.electronic_journal.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Boolean existsByTitle(String title);
}
