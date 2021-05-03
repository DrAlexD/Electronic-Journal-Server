package com.example.electronic_journal.controller;

import com.example.electronic_journal.model.Student;
import com.example.electronic_journal.model.Subject;
import com.example.electronic_journal.model.SubjectInfo;
import com.example.electronic_journal.repository.SubjectInfoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SubjectInfoController {

    private final SubjectInfoRepository subjectInfoRepository;

    public SubjectInfoController(SubjectInfoRepository subjectInfoRepository) {
        this.subjectInfoRepository = subjectInfoRepository;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-students")
    public ResponseEntity<List<Student>> getAvailableStudents(@RequestParam Long professorId, @RequestParam Long semesterId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>(subjectInfoRepository
                .findByProfessorIdAndSemesterId(professorId, semesterId));

        List<Student> students = subjectInfos.stream()
                .map(SubjectInfo::getGroup)
                .flatMap(group -> group.getStudents().stream())
                .sorted(Comparator.comparing(Student::getId))
                .collect(Collectors.toList());

        if (students.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-subjects")
    public ResponseEntity<List<Subject>> getAvailableSubjects(@RequestParam Long professorId, @RequestParam Long semesterId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>(subjectInfoRepository
                .findByProfessorIdAndSemesterId(professorId, semesterId));

        List<Subject> subjects = subjectInfos.stream()
                .map(SubjectInfo::getSubject)
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-groups-in-subject")
    public ResponseEntity<List<SubjectInfo>> getAvailableGroupsInSubject(@RequestParam Long professorId, @RequestParam Long semesterId,
                                                                         @RequestParam Long subjectId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>(subjectInfoRepository
                .findByProfessorIdAndSemesterIdAndSubjectId(professorId, semesterId, subjectId));

        if (subjectInfos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjectInfos, HttpStatus.OK);
    }

    /*@PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-subjects-with-groups")
    public ResponseEntity<Map<Subject, List<SubjectInfo>>> getAvailableSubjectsWithGroups(@RequestParam Long professorId, @RequestParam Long semesterId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>(subjectInfoRepository
                .findByProfessorIdAndSemesterId(professorId, semesterId));

        Map<Subject, List<SubjectInfo>> subjectsWithGroups = subjectInfos.stream()
                .map(SubjectInfo::getSubject)
                .collect(Collectors.toMap(subject -> subject,
                        subject -> subjectInfoRepository
                                .findByProfessorIdAndSemesterIdAndSubjectId(professorId, semesterId, subject.getId())));

        if (subjectsWithGroups.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjectsWithGroups, HttpStatus.OK);
    }*/
}