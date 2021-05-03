package com.example.electronic_journal.controller;

import com.example.electronic_journal.model.StudentPerformanceInSubject;
import com.example.electronic_journal.model.Subject;
import com.example.electronic_journal.model.SubjectInfo;
import com.example.electronic_journal.repository.StudentPerformanceInSubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StudentPerformanceInSubjectController {

    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;

    public StudentPerformanceInSubjectController(StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository) {
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-student-subjects")
    public ResponseEntity<List<Subject>> getAvailableStudentSubjects(@RequestParam Long studentId, @RequestParam Long semesterId) {
        List<StudentPerformanceInSubject> studentPerformanceInSubjects = new ArrayList<>(studentPerformanceInSubjectRepository
                .findByProfessorIdAndSemesterId(studentId, semesterId));

        List<Subject> subjects = studentPerformanceInSubjects.stream()
                .map(StudentPerformanceInSubject::getSubjectInfo)
                .map(SubjectInfo::getSubject)
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }
}