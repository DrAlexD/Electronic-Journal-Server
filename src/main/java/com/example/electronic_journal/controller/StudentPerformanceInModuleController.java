package com.example.electronic_journal.controller;

import com.example.electronic_journal.model.StudentPerformanceInModule;
import com.example.electronic_journal.repository.StudentPerformanceInModuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("/api")
public class StudentPerformanceInModuleController {

    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;

    public StudentPerformanceInModuleController(StudentPerformanceInModuleRepository studentPerformanceInModuleRepository) {
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/student-performance-in-modules")
    public ResponseEntity<Map<String, StudentPerformanceInModule>> getStudentPerformanceInModules(@RequestParam Long studentPerformanceInSubjectId) {
        List<StudentPerformanceInModule> studentPerformance = new ArrayList<>(
                studentPerformanceInModuleRepository.findByStudentPerformanceInSubjectId(studentPerformanceInSubjectId));

        Map<String, StudentPerformanceInModule> studentPerformanceByModuleNumber = studentPerformance.stream().
                collect(Collectors.toMap(s -> String.valueOf(s.getModule().getModuleNumber()), s -> s));

        if (studentPerformanceByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentPerformanceByModuleNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-performances-in-modules")
    public ResponseEntity<Map<String, List<StudentPerformanceInModule>>> getStudentsPerformancesInModules(@RequestParam Long subjectInfoId) {
        List<StudentPerformanceInModule> studentsPerformances = new ArrayList<>(
                studentPerformanceInModuleRepository.findBySubjectInfoId(subjectInfoId));

        Map<String, List<StudentPerformanceInModule>> studentsPerformancesByModuleNumber = studentsPerformances.stream()
                .sorted(Comparator.comparing(s -> s.getStudentPerformanceInSubject().getStudent().getSecondName()))
                .collect(groupingBy(e -> String.valueOf(e.getModule().getModuleNumber())));

        if (studentsPerformancesByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentsPerformancesByModuleNumber, HttpStatus.OK);
    }
}