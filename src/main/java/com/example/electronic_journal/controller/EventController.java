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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventRepository eventRepository;
    private final StudentEventRepository studentEventRepository;
    private final ModuleRepository moduleRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;
    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;

    public EventController(EventRepository eventRepository, StudentEventRepository studentEventRepository,
                           ModuleRepository moduleRepository, StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                           StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository) {
        this.eventRepository = eventRepository;
        this.studentEventRepository = studentEventRepository;
        this.moduleRepository = moduleRepository;
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/events")
    public ResponseEntity<Map<String, List<Event>>> getEvents(@RequestParam Long subjectInfoId) {
        List<Event> events = new ArrayList<>(eventRepository.findBySubjectInfoId(subjectInfoId));

        Map<String, List<Event>> eventsByEventNumber = events.stream()
                .sorted(Comparator.comparing(Event::getStartDate))
                .collect(groupingBy(e -> String.valueOf(e.getModule().getModuleNumber())));

        if (eventsByEventNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(eventsByEventNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable("id") long id) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Event with id = " + id));

        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/events")
    public ResponseEntity<Integer> createEvent(@RequestBody Event event) {
        List<Event> events = eventRepository.findByModuleId(event.getModule().getId());
        Integer sum = events.stream().map(Event::getMinPoints)
                .reduce(0, Integer::sum);

        if (sum + event.getMinPoints() > event.getModule().getMinPoints()) {
            Integer sum2 = event.getModule().getMinPoints() - sum;
            return new ResponseEntity<>(sum2, HttpStatus.NOT_ACCEPTABLE);
        } else {
            Event returnedEvent = eventRepository.save(new Event(event.getModule(), event.getType(), event.getNumber(), event.getStartDate(),
                    event.getDeadlineDate(), event.getMinPoints(), event.getMaxPoints()));

            Module module = returnedEvent.getModule();
            if (module.getMaxAvailablePoints() != null)
                module.setMaxAvailablePoints(module.getMaxAvailablePoints() + returnedEvent.getMaxPoints());
            else
                module.setMaxAvailablePoints(returnedEvent.getMaxPoints());
            moduleRepository.save(module);

            return new ResponseEntity<>(0, HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/last-number-of-event-type")
    public ResponseEntity<Integer> getLastNumberOfEventType(@RequestParam Long subjectInfoId,
                                                            @RequestParam Integer type) {
        List<Event> events = new ArrayList<>(eventRepository.findBySubjectInfoIdAndType(subjectInfoId, type));

        Integer lastNumber = 0;
        for (Event event : events) {
            if (event.getNumber() > lastNumber) {
                lastNumber = event.getNumber();
            }
        }

        return new ResponseEntity<>(lastNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/events/{id}")
    public ResponseEntity<Integer> updateEvent(@PathVariable("id") long id, @RequestBody Event event) {
        Event _event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + id));

        List<Event> events = eventRepository.findByModuleId(event.getModule().getId());
        Integer sum = events.stream().map(Event::getMinPoints)
                .reduce(0, Integer::sum);

        if (sum + event.getMinPoints() - _event.getMinPoints() > event.getModule().getMinPoints()) {
            return new ResponseEntity<>(event.getModule().getMinPoints() - sum + _event.getMinPoints(), HttpStatus.NOT_ACCEPTABLE);
        } else {
            Integer maxPointsBefore = _event.getMaxPoints();

            _event.setModule(event.getModule());
            _event.setType(event.getType());
            _event.setNumber(event.getNumber());
            _event.setStartDate(event.getStartDate());
            _event.setDeadlineDate(event.getDeadlineDate());
            _event.setMinPoints(event.getMinPoints());
            _event.setNumberOfVariants(event.getNumberOfVariants());

            _event.setMaxPoints(event.getMaxPoints());
            Event returnedEvent = eventRepository.save(_event);

            if (!returnedEvent.getMaxPoints().equals(maxPointsBefore)) {
                Module module = returnedEvent.getModule();
                module.setMaxAvailablePoints(module.getMaxAvailablePoints() + returnedEvent.getMaxPoints() - maxPointsBefore);
                moduleRepository.save(module);
            }

            return new ResponseEntity<>(0, HttpStatus.OK);
        }
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
    @DeleteMapping("/events/{id}")
    public ResponseEntity<HttpStatus> deleteEvent(@PathVariable("id") long id) {
        Event _event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + id));

        Module module = _event.getModule();
        module.setMaxAvailablePoints(module.getMaxAvailablePoints() - _event.getMaxPoints());
        moduleRepository.save(module);

        List<StudentEvent> studentEventsAllStudents = studentEventRepository.findByEventId(id);
        List<Long> studentIds = studentEventsAllStudents.stream().map(s -> s.getStudentPerformanceInModule()
                .getStudentPerformanceInSubject().getStudent().getId()).distinct().collect(Collectors.toList());
        Map<Long, List<StudentEvent>> studentEventsByStudent = studentEventsAllStudents.stream().collect(groupingBy(s -> s.getStudentPerformanceInModule().getStudentPerformanceInSubject().getId()));

        eventRepository.deleteById(id);

        for (Long studentId : studentIds) {
            List<StudentEvent> studentEvents = studentEventsByStudent.get(studentId);

            Integer lastAttempt = 0;
            StudentEvent studentEventChosen = null;
            for (StudentEvent se : studentEvents) {
                if (se.getAttemptNumber() > lastAttempt) {
                    lastAttempt = se.getAttemptNumber();
                    studentEventChosen = se;
                }
            }

            StudentPerformanceInModule studentPerformanceInModule = studentEventChosen.getStudentPerformanceInModule();
            StudentPerformanceInSubject studentPerformanceInSubject = studentEventChosen.getStudentPerformanceInModule().getStudentPerformanceInSubject();

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
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}