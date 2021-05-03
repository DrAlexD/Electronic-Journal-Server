package com.example.electronic_journal.security.services;

import com.example.electronic_journal.model.Professor;
import com.example.electronic_journal.model.Student;
import com.example.electronic_journal.repository.ProfessorRepository;
import com.example.electronic_journal.repository.StudentRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;

    public UserDetailsServiceImpl(ProfessorRepository professorRepository, StudentRepository studentRepository) {
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Professor> professor = professorRepository.findByUsername(username);

        if (professor.isPresent()) {
            return UserDetailsImpl.build(professor.get());
        } else {
            Student student = studentRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
            return UserDetailsImpl.build(student);
        }
    }
}