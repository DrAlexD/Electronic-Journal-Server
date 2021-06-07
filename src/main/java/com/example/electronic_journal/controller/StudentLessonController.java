package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.*;
import com.example.electronic_journal.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("/api")
public class StudentLessonController {

    private final EventRepository eventRepository;
    private final StudentEventRepository studentEventRepository;
    private final StudentLessonRepository studentLessonRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;
    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;

    public StudentLessonController(EventRepository eventRepository, StudentEventRepository studentEventRepository,
                                   StudentLessonRepository studentLessonRepository,
                                   StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                                   StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository) {
        this.eventRepository = eventRepository;
        this.studentEventRepository = studentEventRepository;
        this.studentLessonRepository = studentLessonRepository;
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/student-lessons")
    public ResponseEntity<Map<String, List<StudentLesson>>> getStudentLessons(@RequestParam Long studentPerformanceInSubjectId) {
        List<StudentLesson> studentLessons = new ArrayList<>(
                studentLessonRepository.findByStudentPerformanceInSubjectId(studentPerformanceInSubjectId));

        Map<String, List<StudentLesson>> studentLessonsByModuleNumber = studentLessons.stream()
                .sorted(Comparator.comparing(s -> s.getLesson().getDateAndTime()))
                .collect(groupingBy(s -> String.valueOf(s.getLesson().getModule().getModuleNumber())));

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

        Map<String, Map<String, List<StudentLesson>>> studentsLessonsByModuleNumber = studentsLessons.stream()
                .sorted((o1, o2) -> {
                    if (o1.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getId().equals(
                            o2.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getId())) {
                        return o1.getLesson().getDateAndTime()
                                .compareTo(o2.getLesson().getDateAndTime());
                    } else {
                        return o1.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getSecondName()
                                .compareTo(o2.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getSecondName());
                    }
                })
                .collect(groupingBy(e -> String.valueOf(e.getLesson().getModule().getModuleNumber()),
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

    private void defineIsHasModuleCredit(StudentPerformanceInModule s) {
        boolean isHaveCredit = true;
        if (s.getEarnedPoints() != null) {
            if (s.getEarnedPoints() >= s.getModule().getMinPoints()) {
                List<Event> events = eventRepository.findByModuleId(s.getModule().getId());

                for (Event e : events) {
                    List<StudentEvent> studentEvents = studentEventRepository.findByStudentPerformanceInModuleIdAndEventId(s.getId(), e.getId());

                    Integer lastAttempt = 0;
                    StudentEvent studentEventChosen = null;
                    for (StudentEvent se : studentEvents) {
                        if (se.getAttemptNumber() > lastAttempt) {
                            lastAttempt = se.getAttemptNumber();
                            studentEventChosen = se;
                        }
                    }

                    if (studentEventChosen != null) {
                        if (studentEventChosen.getIsHasCredit() != null && !studentEventChosen.getIsHasCredit()) {
                            isHaveCredit = false;
                            break;
                        }
                    } else {
                        isHaveCredit = false;
                        break;
                    }
                }
            } else
                isHaveCredit = false;
        } else
            isHaveCredit = false;
        s.setIsHasCredit(isHaveCredit);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/students-lessons")
    public ResponseEntity<HttpStatus> createStudentLesson(@RequestBody StudentLesson studentLesson) {
        StudentLesson studentLessonReturned = studentLessonRepository.save(new StudentLesson(studentLesson.getStudentPerformanceInModule(),
                studentLesson.getLesson(), studentLesson.getIsAttended()));

        if (studentLessonReturned.getIsAttended() != null && studentLessonReturned.getIsAttended()) {
            StudentPerformanceInModule studentPerformanceInModule = studentLessonReturned.getStudentPerformanceInModule();
            StudentPerformanceInSubject studentPerformanceInSubject = studentLessonReturned.getStudentPerformanceInModule().getStudentPerformanceInSubject();

            if (studentLessonReturned.getLesson().getPointsPerVisit() != null && studentLessonReturned.getLesson().getPointsPerVisit() != 0) {
                if (studentPerformanceInModule.getEarnedPoints() != null && studentPerformanceInModule.getEarnedPoints() != 0)
                    studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentLessonReturned.getLesson().getPointsPerVisit());
                else
                    studentPerformanceInModule.setEarnedPoints(studentLessonReturned.getLesson().getPointsPerVisit());

                defineIsHasModuleCredit(studentPerformanceInModule);

                if (studentPerformanceInSubject.getEarnedPoints() != null && studentPerformanceInSubject.getEarnedPoints() != 0)
                    studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentLessonReturned.getLesson().getPointsPerVisit());
                else
                    studentPerformanceInSubject.setEarnedPoints(studentLessonReturned.getLesson().getPointsPerVisit());
            }

            studentPerformanceInModuleRepository.save(studentPerformanceInModule);
            studentPerformanceInSubjectRepository.save(studentPerformanceInSubject);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/students-lessons/{id}")
    public ResponseEntity<HttpStatus> updateStudentLesson(@PathVariable("id") long id, @RequestBody StudentLesson studentLesson) {
        StudentLesson _studentLesson = studentLessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentLesson with id = " + id));

        Integer beforeBonusPoints = _studentLesson.getBonusPoints();
        Boolean beforeIsAttended = _studentLesson.getIsAttended();

        _studentLesson.setStudentPerformanceInModule(studentLesson.getStudentPerformanceInModule());
        _studentLesson.setLesson(studentLesson.getLesson());
        _studentLesson.setIsAttended(studentLesson.getIsAttended());
        _studentLesson.setBonusPoints(studentLesson.getBonusPoints());
        StudentLesson studentLessonReturned = studentLessonRepository.save(_studentLesson);

        StudentPerformanceInModule studentPerformanceInModule = studentLessonReturned.getStudentPerformanceInModule();
        StudentPerformanceInSubject studentPerformanceInSubject = studentLessonReturned.getStudentPerformanceInModule().getStudentPerformanceInSubject();

        boolean isChanged = false;

        if (!beforeIsAttended && studentLessonReturned.getIsAttended()) {
            if (studentPerformanceInModule.getEarnedPoints() != null && studentPerformanceInModule.getEarnedPoints() != 0)
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentLessonReturned.getLesson().getPointsPerVisit());
            else
                studentPerformanceInModule.setEarnedPoints(studentLessonReturned.getLesson().getPointsPerVisit());

            if (studentPerformanceInSubject.getEarnedPoints() != null && studentPerformanceInSubject.getEarnedPoints() != 0)
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentLessonReturned.getLesson().getPointsPerVisit());
            else
                studentPerformanceInSubject.setEarnedPoints(studentLessonReturned.getLesson().getPointsPerVisit());

            isChanged = true;
        } else if (beforeIsAttended && !studentLessonReturned.getIsAttended()) {
            studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - studentLessonReturned.getLesson().getPointsPerVisit());
            studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - studentLessonReturned.getLesson().getPointsPerVisit());

            isChanged = true;
        }

        if (beforeBonusPoints != null && beforeBonusPoints != 0) {
            if (studentLessonReturned.getBonusPoints() != null && studentLessonReturned.getBonusPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentLessonReturned.getBonusPoints() - beforeBonusPoints);
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentLessonReturned.getBonusPoints() - beforeBonusPoints);
            } else {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - beforeBonusPoints);
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - beforeBonusPoints);
            }

            isChanged = true;
        } else {
            if (studentLessonReturned.getBonusPoints() != null && studentLessonReturned.getBonusPoints() != 0) {
                if (studentPerformanceInModule.getEarnedPoints() != null && studentPerformanceInModule.getEarnedPoints() != 0)
                    studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentLessonReturned.getBonusPoints());
                else
                    studentPerformanceInModule.setEarnedPoints(studentLessonReturned.getBonusPoints());

                isChanged = true;

                if (studentPerformanceInSubject.getEarnedPoints() != null && studentPerformanceInSubject.getEarnedPoints() != 0)
                    studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentLessonReturned.getBonusPoints());
                else
                    studentPerformanceInSubject.setEarnedPoints(studentLessonReturned.getBonusPoints());
            }
        }

        if (isChanged)
            defineIsHasModuleCredit(studentPerformanceInModule);

        studentPerformanceInModuleRepository.save(studentPerformanceInModule);
        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/students-lessons/{id}")
    public ResponseEntity<HttpStatus> deleteStudentLesson(@PathVariable("id") long id) {
        StudentLesson studentLesson = studentLessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentLesson with id = " + id));

        if (studentLesson.getIsAttended() != null && studentLesson.getIsAttended()) {
            StudentPerformanceInModule studentPerformanceInModule = studentLesson.getStudentPerformanceInModule();
            StudentPerformanceInSubject studentPerformanceInSubject = studentLesson.getStudentPerformanceInModule().getStudentPerformanceInSubject();

            boolean isChanged = false;
            if (studentLesson.getLesson().getPointsPerVisit() != null && studentLesson.getLesson().getPointsPerVisit() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - studentLesson.getLesson().getPointsPerVisit());

                isChanged = true;

                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - studentLesson.getLesson().getPointsPerVisit());
            }

            if (studentLesson.getBonusPoints() != null && studentLesson.getBonusPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - studentLesson.getBonusPoints());

                isChanged = true;

                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - studentLesson.getBonusPoints());
            }

            if (isChanged)
                defineIsHasModuleCredit(studentPerformanceInModule);

            studentPerformanceInModuleRepository.save(studentPerformanceInModule);
            studentPerformanceInSubjectRepository.save(studentPerformanceInSubject);
        }

        studentLessonRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}