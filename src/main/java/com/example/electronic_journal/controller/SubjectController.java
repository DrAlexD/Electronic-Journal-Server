package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Subject;
import com.example.electronic_journal.repository.SubjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>(subjectRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));

        if (subjects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @GetMapping("/subjects/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable("id") long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Subject with id = " + id));

        return new ResponseEntity<>(subject, HttpStatus.OK);
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        Subject _subject = subjectRepository
                .save(new Subject(subject.getTitle()));
        return new ResponseEntity<>(_subject, HttpStatus.CREATED);
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable("id") long id, @RequestBody Subject subject) {
        Subject _subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Subject with id = " + id));

        _subject.setTitle(subject.getTitle());

        return new ResponseEntity<>(subjectRepository.save(_subject), HttpStatus.OK);
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<HttpStatus> deleteSubject(@PathVariable("id") long id) {
        subjectRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}