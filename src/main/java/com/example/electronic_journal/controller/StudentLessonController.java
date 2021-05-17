package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.StudentLesson;
import com.example.electronic_journal.repository.StudentLessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("/api")
public class StudentLessonController {

    private final StudentLessonRepository studentLessonRepository;

    public StudentLessonController(StudentLessonRepository studentLessonRepository) {
        this.studentLessonRepository = studentLessonRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/student-lessons")
    public ResponseEntity<Map<String, List<StudentLesson>>> getStudentLessons(@RequestParam Long studentPerformanceInSubjectId) {
        List<StudentLesson> studentLessons = new ArrayList<>(
                studentLessonRepository.findByStudentPerformanceInSubjectId(studentPerformanceInSubjectId));

        Map<String, List<StudentLesson>> studentLessonsByModuleNumber = studentLessons.stream().
                collect(groupingBy(s -> String.valueOf(s.getLesson().getModule().getModuleNumber())));

        if (studentLessonsByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentLessonsByModuleNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-lessons")
    public ResponseEntity<Map<String, Map<String, List<StudentLesson>>>> getStudentsLessons(@RequestParam Long subjectInfoId) {
        List<StudentLesson> studentsLessons = new ArrayList<>(
                studentLessonRepository.findBySubjectInfoId(subjectInfoId));

        Map<String, Map<String, List<StudentLesson>>> studentsLessonsByModuleNumber = studentsLessons.stream().collect(
                groupingBy(e -> String.valueOf(e.getLesson().getModule().getModuleNumber()),
                        groupingBy(s -> String.valueOf(s.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getId()))));

        if (studentsLessonsByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentsLessonsByModuleNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-lessons/{id}")
    public ResponseEntity<StudentLesson> getStudentLessonById(@PathVariable("id") long id) {
        StudentLesson studentLesson = studentLessonRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found StudentLesson with id = " + id));

        return new ResponseEntity<>(studentLesson, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/students-lessons")
    public ResponseEntity<HttpStatus> createStudentLesson(@RequestBody StudentLesson studentLesson) {
        studentLessonRepository.save(new StudentLesson(studentLesson.getStudentPerformanceInModule(),
                studentLesson.getLesson(), studentLesson.getIsAttended()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/students-lessons/{id}")
    public ResponseEntity<HttpStatus> updateStudentLesson(@PathVariable("id") long id, @RequestBody StudentLesson studentLesson) {
        StudentLesson _studentLesson = studentLessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentLesson with id = " + id));

        _studentLesson.setStudentPerformanceInModule(studentLesson.getStudentPerformanceInModule());
        _studentLesson.setLesson(studentLesson.getLesson());
        _studentLesson.setIsAttended(studentLesson.getIsAttended());
        _studentLesson.setBonusPoints(studentLesson.getBonusPoints());
        studentLessonRepository.save(_studentLesson);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/students-lessons/{id}")
    public ResponseEntity<HttpStatus> deleteStudentLesson(@PathVariable("id") long id) {
        studentLessonRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}