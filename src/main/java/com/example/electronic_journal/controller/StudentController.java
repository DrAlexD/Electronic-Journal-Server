package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Student;
import com.example.electronic_journal.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;

    public StudentController(StudentRepository studentRepository, PasswordEncoder encoder) {
        this.studentRepository = studentRepository;
        this.encoder = encoder;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) Long groupId) {
        List<Student> students = new ArrayList<>();

        if (groupId == null) {
            students.addAll(studentRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        } else {
            students.addAll(studentRepository.findByGroupId(groupId));
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
        if (studentRepository.existsByUsername(student.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        studentRepository.save(new Student(student.getFirstName(), student.getSecondName(), student.getGroup(), student.getUsername(),
                encoder.encode(student.getPassword())));

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
        _student.setPassword(student.getPassword());
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