package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.*;
import com.example.electronic_journal.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;
    private final ModuleRepository moduleRepository;
    private final SubjectInfoRepository subjectInfoRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder encoder;

    public StudentController(StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository,
                             StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                             ModuleRepository moduleRepository,
                             SubjectInfoRepository subjectInfoRepository, StudentRepository studentRepository,
                             ProfessorRepository professorRepository, PasswordEncoder encoder) {
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
        this.moduleRepository = moduleRepository;
        this.subjectInfoRepository = subjectInfoRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.encoder = encoder;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) Long groupId) {
        List<Student> students = new ArrayList<>();

        if (groupId == null) {
            students.addAll(studentRepository.findAll().stream()
                    .sorted((o1, o2) -> {
                        if (o1.getGroup().getId().equals(o2.getGroup().getId())) {
                            return o1.getSecondName().compareTo(o2.getSecondName());
                        } else {
                            return o1.getGroup().getTitle().compareTo(o2.getGroup().getTitle());
                        }
                    })
                    .collect(Collectors.toList()));
        } else {
            students.addAll(studentRepository.findByGroupId(groupId).stream()
                    .sorted((o1, o2) -> {
                        if (o1.getGroup().getId().equals(o2.getGroup().getId())) {
                            return o1.getSecondName().compareTo(o2.getSecondName());
                        } else {
                            return o1.getGroup().getTitle().compareTo(o2.getGroup().getTitle());
                        }
                    })
                    .collect(Collectors.toList()));
        }

        if (students.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") long id) {
        Student student = studentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Student with id = " + id));

        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/students")
    public ResponseEntity<HttpStatus> createStudent(@RequestBody Student student) {
        if (studentRepository.existsByUsername(student.getUsername()) ||
                professorRepository.existsByUsername(student.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Student returnedStudent = studentRepository.save(new Student(student.getFirstName(), student.getSecondName(), student.getGroup(), student.getUsername(),
                encoder.encode(student.getPassword())));

        List<SubjectInfo> subjectInfos = subjectInfoRepository.findByGroupId(student.getGroup().getId());

        for (SubjectInfo subjectInfo : subjectInfos) {
            StudentPerformanceInSubject studentPerformanceInSubjectReturned = studentPerformanceInSubjectRepository.save(new StudentPerformanceInSubject(subjectInfo, returnedStudent));
            List<Module> modules = moduleRepository.findBySubjectInfoId(subjectInfo.getId());

            studentPerformanceInModuleRepository.save(new StudentPerformanceInModule(modules.get(0), studentPerformanceInSubjectReturned));
            studentPerformanceInModuleRepository.save(new StudentPerformanceInModule(modules.get(1), studentPerformanceInSubjectReturned));
            studentPerformanceInModuleRepository.save(new StudentPerformanceInModule(modules.get(2), studentPerformanceInSubjectReturned));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/students/{id}")
    public ResponseEntity<HttpStatus> updateStudent(@PathVariable("id") long id, @RequestBody Student student) {
        Student _student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Student with id = " + id));

        if (!_student.getUsername().equals(student.getUsername()) && studentRepository.existsByUsername(student.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            _student.setUsername(student.getUsername());
        }
        _student.setFirstName(student.getFirstName());
        _student.setSecondName(student.getSecondName());
        _student.setPassword(encoder.encode(student.getPassword()));
        _student.setGroup(student.getGroup());
        studentRepository.save(_student);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/students/{id}")
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable("id") long id) {
        studentRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}