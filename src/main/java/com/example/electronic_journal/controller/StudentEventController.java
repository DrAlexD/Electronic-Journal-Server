package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Event;
import com.example.electronic_journal.model.StudentEvent;
import com.example.electronic_journal.model.StudentPerformanceInModule;
import com.example.electronic_journal.model.StudentPerformanceInSubject;
import com.example.electronic_journal.repository.EventRepository;
import com.example.electronic_journal.repository.StudentEventRepository;
import com.example.electronic_journal.repository.StudentPerformanceInModuleRepository;
import com.example.electronic_journal.repository.StudentPerformanceInSubjectRepository;
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
    private final EventRepository eventRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;
    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;

    public StudentEventController(StudentEventRepository studentEventRepository,
                                  EventRepository eventRepository,
                                  StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                                  StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository) {
        this.studentEventRepository = studentEventRepository;
        this.eventRepository = eventRepository;
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/student-events")
    public ResponseEntity<Map<String, List<StudentEvent>>> getStudentEvents(@RequestParam Long studentPerformanceInSubjectId) {
        List<StudentEvent> studentEvents = new ArrayList<>(
                studentEventRepository.findByStudentPerformanceInSubjectId(studentPerformanceInSubjectId));

        Map<String, List<StudentEvent>> studentEventsByModuleNumber = studentEvents.stream()
                .sorted((o1, o2) -> {
                    if (o1.getEvent().getStartDate().equals(o2.getEvent().getStartDate())) {
                        return o1.getAttemptNumber().compareTo(o2.getAttemptNumber());
                    } else {
                        return o1.getEvent().getStartDate()
                                .compareTo(o2.getEvent().getStartDate());
                    }
                })
                .collect(groupingBy(s -> String.valueOf(s.getEvent().getModule().getModuleNumber())));

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

        Map<String, Map<String, List<StudentEvent>>> studentsEventsByModuleNumber = studentsEvents.stream()
                .sorted((o1, o2) -> {
                    if (o1.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getId().equals(
                            o2.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getId())) {
                        if (o1.getEvent().getStartDate().equals(o2.getEvent().getStartDate())) {
                            return o1.getAttemptNumber().compareTo(o2.getAttemptNumber());
                        } else {
                            return o1.getEvent().getStartDate()
                                    .compareTo(o2.getEvent().getStartDate());
                        }
                    } else {
                        return o1.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getSecondName()
                                .compareTo(o2.getStudentPerformanceInModule().getStudentPerformanceInSubject().getStudent().getSecondName());
                    }
                })
                .collect(groupingBy(e -> String.valueOf(e.getEvent().getModule().getModuleNumber()),
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
    @PostMapping("/students-events")
    public ResponseEntity<HttpStatus> createStudentEvent(@RequestBody StudentEvent studentEvent) {
        List<StudentEvent> studentEvents = studentEventRepository.findByStudentPerformanceInModuleIdAndEventId(studentEvent.getStudentPerformanceInModule().getId(),
                studentEvent.getEvent().getId());

        Integer lastAttempt = 0;
        StudentEvent studentEventChosen = null;
        for (StudentEvent se : studentEvents) {
            if (se.getAttemptNumber() > lastAttempt) {
                lastAttempt = se.getAttemptNumber();
                studentEventChosen = se;
            }
        }

        StudentEvent studentEventReturned = studentEventRepository.save(new StudentEvent(studentEvent.getAttemptNumber(), studentEvent.getStudentPerformanceInModule(),
                studentEvent.getEvent(), studentEvent.getIsAttended()));

        StudentPerformanceInModule studentPerformanceInModule = studentEventReturned.getStudentPerformanceInModule();
        StudentPerformanceInSubject studentPerformanceInSubject = studentEventReturned.getStudentPerformanceInModule().getStudentPerformanceInSubject();

        if (lastAttempt != 0) {
            if (studentEventChosen.getEarnedPoints() != null && studentEventChosen.getEarnedPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - studentEventChosen.getEarnedPoints());
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - studentEventChosen.getEarnedPoints());
            }

            if (studentEventChosen.getBonusPoints() != null && studentEventChosen.getBonusPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - studentEventChosen.getBonusPoints());
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - studentEventChosen.getBonusPoints());
            }
        }

        defineIsHasModuleCredit(studentPerformanceInModule);

        studentPerformanceInModuleRepository.save(studentPerformanceInModule);
        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/students-events/{id}")
    public ResponseEntity<HttpStatus> updateStudentEvent(@PathVariable("id") long id, @RequestBody StudentEvent studentEvent) {
        StudentEvent _studentEvent = studentEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentEvent with id = " + id));

        Integer beforeEarnedPoints = _studentEvent.getEarnedPoints();
        Integer beforeBonusPoints = _studentEvent.getBonusPoints();
        Boolean beforeIsHaveCredit = _studentEvent.getIsHasCredit();

        _studentEvent.setAttemptNumber(studentEvent.getAttemptNumber());
        _studentEvent.setStudentPerformanceInModule(studentEvent.getStudentPerformanceInModule());
        _studentEvent.setEvent(studentEvent.getEvent());
        _studentEvent.setIsAttended(studentEvent.getIsAttended());
        _studentEvent.setVariantNumber(studentEvent.getVariantNumber());
        _studentEvent.setFinishDate(studentEvent.getFinishDate());
        _studentEvent.setEarnedPoints(studentEvent.getEarnedPoints());
        _studentEvent.setBonusPoints(studentEvent.getBonusPoints());
        _studentEvent.setIsHasCredit(studentEvent.getIsHasCredit());
        StudentEvent studentEventReturned = studentEventRepository.save(_studentEvent);

        StudentPerformanceInModule studentPerformanceInModule = studentEventReturned.getStudentPerformanceInModule();
        StudentPerformanceInSubject studentPerformanceInSubject = studentEventReturned.getStudentPerformanceInModule().getStudentPerformanceInSubject();

        boolean isChanged = false;
        if (beforeEarnedPoints != null && beforeEarnedPoints != 0) {
            isChanged = true;
            if (studentEvent.getEarnedPoints() != null && studentEvent.getEarnedPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentEvent.getEarnedPoints() - beforeEarnedPoints);
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentEvent.getEarnedPoints() - beforeEarnedPoints);
            } else {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - beforeEarnedPoints);
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - beforeEarnedPoints);
            }
        } else {
            if (studentEvent.getEarnedPoints() != null && studentEvent.getEarnedPoints() != 0) {
                if (studentPerformanceInModule.getEarnedPoints() != null && studentPerformanceInModule.getEarnedPoints() != 0)
                    studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentEvent.getEarnedPoints());
                else
                    studentPerformanceInModule.setEarnedPoints(studentEvent.getEarnedPoints());

                isChanged = true;

                if (studentPerformanceInSubject.getEarnedPoints() != null && studentPerformanceInSubject.getEarnedPoints() != 0)
                    studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentEvent.getEarnedPoints());
                else
                    studentPerformanceInSubject.setEarnedPoints(studentEvent.getEarnedPoints());
            }
        }

        if (beforeBonusPoints != null && beforeBonusPoints != 0) {
            if (studentEvent.getBonusPoints() != null && studentEvent.getBonusPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentEvent.getBonusPoints() - beforeBonusPoints);
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentEvent.getBonusPoints() - beforeBonusPoints);
            } else {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - beforeBonusPoints);
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - beforeBonusPoints);
            }

            isChanged = true;
        } else {
            if (studentEvent.getBonusPoints() != null && studentEvent.getBonusPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentEvent.getBonusPoints());

                isChanged = true;

                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentEvent.getBonusPoints());
            }
        }

        if (beforeIsHaveCredit != null) {
            if (studentEvent.getIsHasCredit() != null) {
                if ((beforeIsHaveCredit.equals(Boolean.FALSE) && studentEvent.getIsHasCredit().equals(Boolean.TRUE)) ||
                        (beforeIsHaveCredit.equals(Boolean.TRUE) && studentEvent.getIsHasCredit().equals(Boolean.FALSE))) {
                    isChanged = true;
                }
            }
        } else {
            if (studentEvent.getIsHasCredit() != null) {
                isChanged = true;
            }
        }

        if (isChanged)
            defineIsHasModuleCredit(studentPerformanceInModule);

        studentPerformanceInModuleRepository.save(studentPerformanceInModule);
        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/students-events/{id}")
    public ResponseEntity<HttpStatus> deleteStudentEvent(@PathVariable("id") long id) {
        StudentEvent _studentEvent = studentEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found StudentEvent with id = " + id));

        StudentPerformanceInModule studentPerformanceInModule = _studentEvent.getStudentPerformanceInModule();
        StudentPerformanceInSubject studentPerformanceInSubject = _studentEvent.getStudentPerformanceInModule().getStudentPerformanceInSubject();
        Long eventId = _studentEvent.getEvent().getId();

        if (_studentEvent.getEarnedPoints() != null && _studentEvent.getEarnedPoints() != 0) {
            studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - _studentEvent.getEarnedPoints());
            studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - _studentEvent.getEarnedPoints());
        }

        if (_studentEvent.getBonusPoints() != null && _studentEvent.getBonusPoints() != 0) {
            studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() - _studentEvent.getBonusPoints());
            studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() - _studentEvent.getBonusPoints());
        }

        studentEventRepository.deleteById(id);

        List<StudentEvent> studentEvents = studentEventRepository.findByStudentPerformanceInModuleIdAndEventId(studentPerformanceInModule.getId(),
                eventId);

        Integer lastAttempt = 0;
        StudentEvent studentEventChosen = null;
        for (StudentEvent se : studentEvents) {
            if (se.getAttemptNumber() > lastAttempt) {
                lastAttempt = se.getAttemptNumber();
                studentEventChosen = se;
            }
        }

        if (lastAttempt != 0) {
            if (studentEventChosen.getEarnedPoints() != null && studentEventChosen.getEarnedPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentEventChosen.getEarnedPoints());
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentEventChosen.getEarnedPoints());
            }

            if (studentEventChosen.getBonusPoints() != null && studentEventChosen.getBonusPoints() != 0) {
                studentPerformanceInModule.setEarnedPoints(studentPerformanceInModule.getEarnedPoints() + studentEventChosen.getBonusPoints());
                studentPerformanceInSubject.setEarnedPoints(studentPerformanceInSubject.getEarnedPoints() + studentEventChosen.getBonusPoints());
            }
        }

        defineIsHasModuleCredit(studentPerformanceInModule);

        studentPerformanceInModuleRepository.save(studentPerformanceInModule);
        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}