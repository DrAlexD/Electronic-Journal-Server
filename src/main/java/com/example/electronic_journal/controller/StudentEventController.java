package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.StudentEvent;
import com.example.electronic_journal.repository.StudentEventRepository;
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
public class StudentEventController {

    private final StudentEventRepository studentEventRepository;

    public StudentEventController(StudentEventRepository studentEventRepository) {
        this.studentEventRepository = studentEventRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/student-events")
    public ResponseEntity<Map<String, List<StudentEvent>>> getStudentEvents(@RequestParam Long studentPerformanceInSubjectId) {
        List<StudentEvent> studentEvents = new ArrayList<>(
                studentEventRepository.findByStudentPerformanceInSubjectId(studentPerformanceInSubjectId));

        Map<String, List<StudentEvent>> studentEventsByModuleNumber = studentEvents.stream().
                collect(groupingBy(s -> String.valueOf(s.getEvent().getModule().getModuleNumber())));

        if (studentEventsByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentEventsByModuleNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-events")
    public ResponseEntity<Map<String, Map<String, List<StudentEvent>>>> getStudentsEvents(@RequestParam Long subjectInfoId) {
        List<StudentEvent> studentsEvents = new ArrayList<>(
                studentEventRepository.findBySubjectInfoId(subjectInfoId));

        Map<String, Map<String, List<StudentEvent>>> studentsEventsByModuleNumber = studentsEvents.stream().collect(
                groupingBy(e -> String.valueOf(e.getEvent().getModule().getModuleNumber()),
                        groupingBy(s -> String.valueOf(s.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getId()))));

        if (studentsEventsByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(studentsEventsByModuleNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students-events/{id}")
    public ResponseEntity<StudentEvent> getStudentEventById(@PathVariable("id") long id) {
        StudentEvent studentEvent = studentEventRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found StudentEvent with id = " + id));

        return new ResponseEntity<>(studentEvent, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/students-events")
    public ResponseEntity<HttpStatus> createStudentEvent(@RequestBody StudentEvent studentEvent) {
        studentEventRepository.save(new StudentEvent(studentEvent.getAttemptNumber(), studentEvent.getStudentPerformanceInModule(),
                studentEvent.getEvent(), studentEvent.getIsAttended(), studentEvent.getVariantNumber(),
                studentEvent.getFinishDate(), studentEvent.getEarnedPoints(), studentEvent.getBonusPoints(),
                studentEvent.getIsHaveCredit()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/students-events/{id}")
    public ResponseEntity<HttpStatus> updateStudentEvent(@PathVariable("id") long id, @RequestBody StudentEvent studentEvent) {
        StudentEvent _studentEvent = studentEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentEvent with id = " + id));

        _studentEvent.setAttemptNumber(studentEvent.getAttemptNumber());
        _studentEvent.setStudentPerformanceInModule(studentEvent.getStudentPerformanceInModule());
        _studentEvent.setEvent(studentEvent.getEvent());
        _studentEvent.setIsAttended(studentEvent.getIsAttended());
        _studentEvent.setVariantNumber(studentEvent.getVariantNumber());
        _studentEvent.setFinishDate(studentEvent.getFinishDate());
        _studentEvent.setEarnedPoints(studentEvent.getEarnedPoints());
        _studentEvent.setBonusPoints(studentEvent.getBonusPoints());
        _studentEvent.setIsHaveCredit(studentEvent.getIsHaveCredit());
        /*_studentEvent.setIsHaveCredit(studentEvent.getEarnedPoints() + studentEvent.getBonusPoints() >
                studentEvent.getEvent().getMinPoints());*/
        studentEventRepository.save(_studentEvent);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/students-events/{id}")
    public ResponseEntity<HttpStatus> deleteStudentEvent(@PathVariable("id") long id) {
        studentEventRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}