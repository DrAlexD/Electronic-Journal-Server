package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.StudentPerformanceInSubject;
import com.example.electronic_journal.model.Subject;
import com.example.electronic_journal.model.SubjectInfo;
import com.example.electronic_journal.repository.StudentPerformanceInSubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        List<StudentPerformanceInSubject> studentPerformance = new ArrayList<>(studentPerformanceInSubjectRepository
                .findByStudentIdAndSemesterId(studentId, semesterId));

        List<Subject> subjects = studentPerformance.stream()
                .map(StudentPerformanceInSubject::getSubjectInfo)
                .map(SubjectInfo::getSubject).distinct()
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-performances-in-subject")
    public ResponseEntity<List<StudentPerformanceInSubject>> getStudentsPerformancesInSubject(@RequestParam Long subjectInfoId) {
        List<StudentPerformanceInSubject> studentsPerformances = new ArrayList<>(
                studentPerformanceInSubjectRepository.findBySubjectInfoId(subjectInfoId));

        if (studentsPerformances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentsPerformances, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-performances-in-subject/{id}")
    public ResponseEntity<StudentPerformanceInSubject> getStudentPerformanceInSubjectById(@PathVariable("id") long id) {
        StudentPerformanceInSubject studentPerformanceInSubject = studentPerformanceInSubjectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found StudentPerformanceInSubject with id = " + id));

        return new ResponseEntity<>(studentPerformanceInSubject, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/students-performances-in-subject/{id}")
    public ResponseEntity<HttpStatus> updateStudentPerformanceInSubject(@PathVariable("id") long id, @RequestBody StudentPerformanceInSubject studentPerformanceInSubject) {
        StudentPerformanceInSubject _studentPerformanceInSubject = studentPerformanceInSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentPerformanceInSubject with id = " + id));

        _studentPerformanceInSubject.setSubjectInfo(studentPerformanceInSubject.getSubjectInfo());
        _studentPerformanceInSubject.setStudent(studentPerformanceInSubject.getStudent());
        _studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints());
        _studentPerformanceInSubject.setBonusPoints(studentPerformanceInSubject.getBonusPoints());
        _studentPerformanceInSubject.setIsHaveCreditOrAdmission(studentPerformanceInSubject.getIsHaveCreditOrAdmission());
        _studentPerformanceInSubject.setEarnedExamPoints(studentPerformanceInSubject.getEarnedExamPoints());
        _studentPerformanceInSubject.setMark(studentPerformanceInSubject.getMark());
        studentPerformanceInSubjectRepository.save(_studentPerformanceInSubject);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}