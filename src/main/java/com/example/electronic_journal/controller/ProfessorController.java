package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Professor;
import com.example.electronic_journal.repository.ProfessorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProfessorController {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder encoder;

    public ProfessorController(ProfessorRepository professorRepository, PasswordEncoder encoder) {
        this.professorRepository = professorRepository;
        this.encoder = encoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/professors")
    public ResponseEntity<List<Professor>> getAllProfessors() {
        List<Professor> professors = new ArrayList<>(professorRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));

        if (professors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(professors, HttpStatus.OK);
    }

    @GetMapping("/professors/{id}")
    public ResponseEntity<Professor> getProfessorById(@PathVariable("id") long id) {
        Professor professor = professorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Professor with id = " + id));

        return new ResponseEntity<>(professor, HttpStatus.OK);
    }

    @PostMapping("/professors")
    public ResponseEntity<HttpStatus> createProfessor(@RequestBody Professor professor) {
        if (professorRepository.existsByUsername(professor.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        professorRepository.save(new Professor(professor.getFirstName(), professor.getSecondName(), professor.getUsername(),
                encoder.encode(professor.getPassword())));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/professors/{id}")
    public ResponseEntity<Professor> updateProfessor(@PathVariable("id") long id, @RequestBody Professor professor) {
        Professor _professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Professor with id = " + id));

        _professor.setFirstName(professor.getFirstName());
        _professor.setSecondName(professor.getSecondName());

        return new ResponseEntity<>(professorRepository.save(_professor), HttpStatus.OK);
    }

    @DeleteMapping("/professors/{id}")
    public ResponseEntity<HttpStatus> deleteProfessor(@PathVariable("id") long id) {
        professorRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*@DeleteMapping("/professors")
    public ResponseEntity<HttpStatus> deleteAllProfessors() {
        professorRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/
}