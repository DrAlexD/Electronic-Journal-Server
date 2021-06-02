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
public class LessonController {

    private final LessonRepository lessonRepository;
    private final EventRepository eventRepository;
    private final ModuleRepository moduleRepository;
    private final StudentEventRepository studentEventRepository;
    private final StudentLessonRepository studentLessonRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;
    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;

    public LessonController(LessonRepository lessonRepository, EventRepository eventRepository,
                            ModuleRepository moduleRepository,
                            StudentEventRepository studentEventRepository,
                            StudentLessonRepository studentLessonRepository,
                            StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                            StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository) {
        this.lessonRepository = lessonRepository;
        this.eventRepository = eventRepository;
        this.moduleRepository = moduleRepository;
        this.studentEventRepository = studentEventRepository;
        this.studentLessonRepository = studentLessonRepository;
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
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

        Map<String, List<Lesson>> lessonsByLessonNumber = lessons.stream()
                .sorted(Comparator.comparing(Lesson::getDateAndTime))
                .collect(groupingBy(l -> String.valueOf(l.getModule().getModuleNumber())));

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
        Lesson returnedLesson = lessonRepository.save(new Lesson(lesson.getModule(), lesson.getDateAndTime(), lesson.getIsLecture(),
                lesson.getPointsPerVisit()));

        Module module = returnedLesson.getModule();
        if (module.getMaxAvailablePoints() != null)
            module.setMaxAvailablePoints(module.getMaxAvailablePoints() + returnedLesson.getPointsPerVisit());
        else
            module.setMaxAvailablePoints(returnedLesson.getPointsPerVisit());
        moduleRepository.save(module);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/lessons/{id}")
    public ResponseEntity<HttpStatus> updateLesson(@PathVariable("id") long id, @RequestBody Lesson lesson) {
        Lesson _lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Lesson with id = " + id));

        Integer beforePointsPerVisit = _lesson.getPointsPerVisit();

        _lesson.setModule(lesson.getModule());
        _lesson.setDateAndTime(lesson.getDateAndTime());
        _lesson.setIsLecture(lesson.getIsLecture());
        _lesson.setPointsPerVisit(lesson.getPointsPerVisit());
        Lesson lessonReturned = lessonRepository.save(_lesson);

        if (!beforePointsPerVisit.equals(lessonReturned.getPointsPerVisit())) {
            Module module = lessonReturned.getModule();
            module.setMaxAvailablePoints(module.getMaxAvailablePoints() + lessonReturned.getPointsPerVisit() - beforePointsPerVisit);
            moduleRepository.save(module);

            List<StudentLesson> studentLessons = studentLessonRepository.findByLessonId(id);
            List<StudentPerformanceInModule> studentPerformancesInModule = studentLessons.stream().map(StudentLesson::getStudentPerformanceInModule).collect(Collectors.toList());

            if (beforePointsPerVisit != 0) {
                if (lessonReturned.getPointsPerVisit() != 0) {
                    for (StudentPerformanceInModule s : studentPerformancesInModule) {
                        StudentPerformanceInSubject ss = s.getStudentPerformanceInSubject();
                        s.setEarnedPoints(s.getEarnedPoints() + lessonReturned.getPointsPerVisit() - beforePointsPerVisit);

                        defineIsHasModuleCredit(s);

                        ss.setEarnedPoints(ss.getEarnedPoints() + lessonReturned.getPointsPerVisit() - beforePointsPerVisit);
                        studentPerformanceInModuleRepository.save(s);
                        studentPerformanceInSubjectRepository.save(ss);
                    }
                } else {
                    for (StudentPerformanceInModule s : studentPerformancesInModule) {
                        StudentPerformanceInSubject ss = s.getStudentPerformanceInSubject();
                        s.setEarnedPoints(s.getEarnedPoints() - beforePointsPerVisit);

                        defineIsHasModuleCredit(s);

                        ss.setEarnedPoints(ss.getEarnedPoints() - beforePointsPerVisit);
                        studentPerformanceInModuleRepository.save(s);
                        studentPerformanceInSubjectRepository.save(ss);
                    }
                }
            } else {
                if (lessonReturned.getPointsPerVisit() != 0) {
                    for (StudentPerformanceInModule s : studentPerformancesInModule) {
                        StudentPerformanceInSubject ss = s.getStudentPerformanceInSubject();
                        s.setEarnedPoints(s.getEarnedPoints() + lessonReturned.getPointsPerVisit());

                        defineIsHasModuleCredit(s);

                        ss.setEarnedPoints(ss.getEarnedPoints() + lessonReturned.getPointsPerVisit());
                        studentPerformanceInModuleRepository.save(s);
                        studentPerformanceInSubjectRepository.save(ss);
                    }
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
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
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<HttpStatus> deleteLesson(@PathVariable("id") long id) {
        Lesson _lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Lesson with id = " + id));

        Module module = _lesson.getModule();
        module.setMaxAvailablePoints(module.getMaxAvailablePoints() - _lesson.getPointsPerVisit());
        moduleRepository.save(module);

        List<StudentLesson> studentLessons = studentLessonRepository.findByLessonId(id);

        for (StudentLesson studentLesson : studentLessons) {
            StudentPerformanceInModule s = studentLesson.getStudentPerformanceInModule();

            if (_lesson.getPointsPerVisit() != 0) {
                StudentPerformanceInSubject ss = s.getStudentPerformanceInSubject();
                if (studentLesson.getBonusPoints() != null && studentLesson.getBonusPoints() != 0) {
                    s.setEarnedPoints(s.getEarnedPoints() - _lesson.getPointsPerVisit() - studentLesson.getBonusPoints());

                    defineIsHasModuleCredit(s);

                    ss.setEarnedPoints(ss.getEarnedPoints() - _lesson.getPointsPerVisit() - studentLesson.getBonusPoints());
                } else {
                    s.setEarnedPoints(s.getEarnedPoints() - _lesson.getPointsPerVisit());

                    defineIsHasModuleCredit(s);

                    ss.setEarnedPoints(ss.getEarnedPoints() - _lesson.getPointsPerVisit());
                }
                studentPerformanceInModuleRepository.save(s);
                studentPerformanceInSubjectRepository.save(ss);
            } else {
                if (studentLesson.getBonusPoints() != null && studentLesson.getBonusPoints() != 0) {
                    StudentPerformanceInSubject ss = s.getStudentPerformanceInSubject();
                    s.setEarnedPoints(s.getEarnedPoints() - studentLesson.getBonusPoints());

                    defineIsHasModuleCredit(s);

                    ss.setEarnedPoints(ss.getEarnedPoints() - studentLesson.getBonusPoints());
                    studentPerformanceInModuleRepository.save(s);
                    studentPerformanceInSubjectRepository.save(ss);
                }
            }
        }

        lessonRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}