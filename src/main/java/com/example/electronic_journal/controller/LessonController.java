package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Lesson;
import com.example.electronic_journal.repository.LessonRepository;
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
public class LessonController {

    private final LessonRepository lessonRepository;

    public LessonController(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/lessons")
    public ResponseEntity<Map<String, List<Lesson>>> getLessons(@RequestParam Long subjectInfoId,
                                                                @RequestParam(required = false) Boolean isLecture) {
        List<Lesson> lessons = new ArrayList<>();

        if (isLecture == null) {
            lessons.addAll(lessonRepository.findBySubjectInfoId(subjectInfoId));
        } else {
            lessons.addAll(lessonRepository.findBySubjectInfoIdAndIsLecture(subjectInfoId, isLecture));
        }

        Map<String, List<Lesson>> lessonsByLessonNumber = lessons.stream().
                collect(groupingBy(l -> String.valueOf(l.getModule().getModuleNumber())));

        if (lessonsByLessonNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(lessonsByLessonNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/lessons/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable("id") long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Lesson with id = " + id));

        return new ResponseEntity<>(lesson, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/lessons")
    public ResponseEntity<HttpStatus> createLesson(@RequestBody Lesson lesson) {
        lessonRepository.save(new Lesson(lesson.getModule(), lesson.getDateAndTime(), lesson.getIsLecture(),
                lesson.getPointsPerVisit()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/lessons/{id}")
    public ResponseEntity<HttpStatus> updateLesson(@PathVariable("id") long id, @RequestBody Lesson lesson) {
        Lesson _lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Lesson with id = " + id));

        _lesson.setModule(lesson.getModule());
        _lesson.setDateAndTime(lesson.getDateAndTime());
        _lesson.setIsLecture(lesson.getIsLecture());
        _lesson.setPointsPerVisit(lesson.getPointsPerVisit());
        lessonRepository.save(_lesson);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<HttpStatus> deleteLesson(@PathVariable("id") long id) {
        lessonRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}