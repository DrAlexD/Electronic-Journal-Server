package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Semester;
import com.example.electronic_journal.repository.SemesterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SemesterController {

    private final SemesterRepository semesterRepository;

    public SemesterController(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/semesters")
    public ResponseEntity<List<Semester>> getAllSemesters() {
        List<Semester> semesters = new ArrayList<>(semesterRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));

        if (semesters.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(semesters, HttpStatus.OK);
    }

    @GetMapping("/semesters/{id}")
    public ResponseEntity<Semester> getSemesterById(@PathVariable("id") long id) {
        Semester semester = semesterRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Semester with id = " + id));

        return new ResponseEntity<>(semester, HttpStatus.OK);
    }

    @PostMapping("/semesters")
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        Semester _semester = semesterRepository
                .save(new Semester(semester.getYear(), semester.getFirstHalf()));
        return new ResponseEntity<>(_semester, HttpStatus.CREATED);
    }

    @PutMapping("/semesters/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable("id") long id, @RequestBody Semester semester) {
        Semester _semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Semester with id = " + id));

        _semester.setYear(semester.getYear());
        _semester.setFirstHalf(semester.getFirstHalf());

        return new ResponseEntity<>(semesterRepository.save(_semester), HttpStatus.OK);
    }

    @DeleteMapping("/semesters/{id}")
    public ResponseEntity<HttpStatus> deleteSemester(@PathVariable("id") long id) {
        semesterRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}