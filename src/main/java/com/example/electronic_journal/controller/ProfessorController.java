package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Professor;
import com.example.electronic_journal.repository.ProfessorRepository;
import com.example.electronic_journal.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProfessorController {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder encoder;

    public ProfessorController(StudentRepository studentRepository, ProfessorRepository professorRepository, PasswordEncoder encoder) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.encoder = encoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/professors")
    public ResponseEntity<List<Professor>> getProfessors() {
        List<Professor> professors = professorRepository.findAll().stream()
                .sorted(Comparator.comparing(Professor::getSecondName)).collect(Collectors.toList());

        if (professors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(professors, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/professors/{id}")
    public ResponseEntity<Professor> getProfessorById(@PathVariable("id") long id) {
        Professor professor = professorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Professor with id = " + id));

        return new ResponseEntity<>(professor, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/professors")
    public ResponseEntity<HttpStatus> createProfessor(@RequestBody Professor professor) {
        if (professorRepository.existsByUsername(professor.getUsername()) ||
                studentRepository.existsByUsername(professor.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        professorRepository.save(new Professor(professor.getFirstName(), professor.getSecondName(), professor.getUsername(),
                encoder.encode(professor.getPassword())));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/professors/{id}")
    public ResponseEntity<HttpStatus> updateProfessor(@PathVariable("id") long id, @RequestBody Professor professor) {
        Professor _professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Professor with id = " + id));

        if (!_professor.getUsername().equals(professor.getUsername()) && professorRepository.existsByUsername(professor.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            _professor.setUsername(professor.getUsername());
        }
        _professor.setFirstName(professor.getFirstName());
        _professor.setSecondName(professor.getSecondName());
        _professor.setPassword(encoder.encode(professor.getPassword()));
        professorRepository.save(_professor);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/professors/{id}")
    public ResponseEntity<HttpStatus> deleteProfessor(@PathVariable("id") long id) {
        professorRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}