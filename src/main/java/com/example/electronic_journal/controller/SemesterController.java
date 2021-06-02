package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Semester;
import com.example.electronic_journal.repository.SemesterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Semester> semesters = semesterRepository.findAll().stream()
                .sorted(Comparator.comparing(Semester::getYear).thenComparing(s -> !s.getIsFirstHalf())).collect(Collectors.toList());

        if (semesters.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(semesters, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/semesters/{id}")
    public ResponseEntity<Semester> getSemesterById(@PathVariable("id") long id) {
        Semester semester = semesterRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Semester with id = " + id));

        return new ResponseEntity<>(semester, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/semesters")
    public ResponseEntity<HttpStatus> createSemester(@RequestBody Semester semester) {
        if (semesterRepository.existsByYearAndIsFirstHalf(semester.getYear(), semester.getIsFirstHalf())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        semesterRepository.save(new Semester(semester.getYear(), semester.getIsFirstHalf()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/semesters/{id}")
    public ResponseEntity<HttpStatus> updateSemester(@PathVariable("id") long id, @RequestBody Semester semester) {
        Semester _semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Semester with id = " + id));

        _semester.setYear(semester.getYear());
        _semester.setIsFirstHalf(semester.getIsFirstHalf());
        semesterRepository.save(_semester);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/semesters/{id}")
    public ResponseEntity<HttpStatus> deleteSemester(@PathVariable("id") long id) {
        semesterRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}