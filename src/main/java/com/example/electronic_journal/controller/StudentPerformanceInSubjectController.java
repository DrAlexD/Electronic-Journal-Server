package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.StudentPerformanceInSubject;
import com.example.electronic_journal.repository.StudentPerformanceInSubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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
    public ResponseEntity<List<StudentPerformanceInSubject>> getAvailableStudentSubjects(@RequestParam Long studentId, @RequestParam Long semesterId) {
        List<StudentPerformanceInSubject> studentPerformance = studentPerformanceInSubjectRepository
                .findByStudentIdAndSemesterId(studentId, semesterId).stream()
                .sorted(Comparator.comparing(s -> s.getSubjectInfo().getSubject().getTitle()))
                .collect(Collectors.toList());

        if (studentPerformance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentPerformance, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-performances-in-subject")
    public ResponseEntity<List<StudentPerformanceInSubject>> getStudentsPerformancesInSubject(@RequestParam Long subjectInfoId) {
        List<StudentPerformanceInSubject> studentsPerformances = studentPerformanceInSubjectRepository.findBySubjectInfoId(subjectInfoId).stream()
                .sorted(Comparator.comparing(s -> s.getStudent().getSecondName()))
                .collect(Collectors.toList());

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
        _studentPerformanceInSubject.setIsHasCreditOrAdmission(studentPerformanceInSubject.getIsHasCreditOrAdmission());

        /*if (!studentPerformanceInSubject.getSubjectInfo().getIsExam()) {
            if (studentPerformanceInSubject.getEarnedPoints() != null && _studentPerformanceInSubject.getIsHaveCreditOrAdmission() == null) {
                if (!studentPerformanceInSubject.getSubjectInfo().getIsDifferentiatedCredit()) {
                    int sumPoints;
                    if (_studentPerformanceInSubject.getBonusPoints() != null) {
                        sumPoints = _studentPerformanceInSubject.getEarnedPoints() + _studentPerformanceInSubject.getBonusPoints();
                    } else{
                        sumPoints = _studentPerformanceInSubject.getEarnedPoints();
                    }
                    _studentPerformanceInSubject.setIsHaveCreditOrAdmission(sumPoints >= 60);
                } else {
                    int sumPoints;
                    if (_studentPerformanceInSubject.getBonusPoints() != null) {
                        sumPoints = _studentPerformanceInSubject.getEarnedPoints() + _studentPerformanceInSubject.getBonusPoints();
                    } else {
                        sumPoints = _studentPerformanceInSubject.getEarnedPoints();
                    }

                    if (sumPoints < 60)
                        _studentPerformanceInSubject.setMark(2);
                    else if (sumPoints < 71)
                        _studentPerformanceInSubject.setMark(3);
                    else if (sumPoints < 84)
                        _studentPerformanceInSubject.setMark(4);
                    else if (sumPoints <= 100)
                        _studentPerformanceInSubject.setMark(5);
                }
            }
        } else*/

        _studentPerformanceInSubject.setEarnedExamPoints(studentPerformanceInSubject.getEarnedExamPoints());
        if (studentPerformanceInSubject.getEarnedExamPoints() != null && _studentPerformanceInSubject.getMark() == null) {
            if (studentPerformanceInSubject.getEarnedExamPoints() < 18) {
                _studentPerformanceInSubject.setMark(2);
            } else {
                int sumPoints;
                if (_studentPerformanceInSubject.getBonusPoints() != null) {
                    sumPoints = _studentPerformanceInSubject.getEarnedPoints() + _studentPerformanceInSubject.getBonusPoints() +
                            _studentPerformanceInSubject.getEarnedExamPoints();
                } else {
                    sumPoints = _studentPerformanceInSubject.getEarnedPoints() + _studentPerformanceInSubject.getEarnedExamPoints();
                }

                if (sumPoints < 60)
                    _studentPerformanceInSubject.setMark(2);
                else if (sumPoints < 71)
                    _studentPerformanceInSubject.setMark(3);
                else if (sumPoints < 84)
                    _studentPerformanceInSubject.setMark(4);
                else if (sumPoints <= 100)
                    _studentPerformanceInSubject.setMark(5);
            }
        } else
            _studentPerformanceInSubject.setMark(studentPerformanceInSubject.getMark());
        studentPerformanceInSubjectRepository.save(_studentPerformanceInSubject);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}