package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Event;
import com.example.electronic_journal.repository.EventRepository;
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
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/events")
    public ResponseEntity<Map<String, List<Event>>> getEvents(@RequestParam Long subjectInfoId) {
        List<Event> events = new ArrayList<>(eventRepository.findBySubjectInfoId(subjectInfoId));

        Map<String, List<Event>> eventsByEventNumber = events.stream().
                collect(groupingBy(e -> String.valueOf(e.getModule().getModuleNumber())));

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
    public ResponseEntity<HttpStatus> createEvent(@RequestBody Event event) {
        eventRepository.save(new Event(event.getModule(), event.getType(), event.getNumber(), event.getStartDate(),
                event.getDeadlineDate(), event.getMinPoints(), event.getMaxPoints()));
        return new ResponseEntity<>(HttpStatus.CREATED);
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
    public ResponseEntity<HttpStatus> updateEvent(@PathVariable("id") long id, @RequestBody Event event) {
        Event _event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Event with id = " + id));

        _event.setModule(event.getModule());
        _event.setType(event.getType());
        _event.setNumber(event.getNumber());
        _event.setStartDate(event.getStartDate());
        _event.setDeadlineDate(event.getDeadlineDate());

        /*        Integer startPoints = _event.getMinPoints();*/
        _event.setMinPoints(event.getMinPoints());
/*
        //TODO добавить repository.save?
        if (!startPoints.equals(event.getMinPoints()))
            _event.getStudentsEvent().forEach(s -> s.setIsHaveCredit(s.getEarnedPoints() + s.getBonusPoints() > event.getMinPoints()));
*/

        _event.setMaxPoints(event.getMaxPoints());
        eventRepository.save(_event);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/events/{id}")
    public ResponseEntity<HttpStatus> deleteEvent(@PathVariable("id") long id) {
        eventRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}