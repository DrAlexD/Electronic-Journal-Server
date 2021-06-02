package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.*;
import com.example.electronic_journal.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SubjectInfoController {

    private final SubjectInfoRepository subjectInfoRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;

    public SubjectInfoController(SubjectInfoRepository subjectInfoRepository, GroupRepository groupRepository, ModuleRepository moduleRepository,
                                 StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository,
                                 StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                                 StudentRepository studentRepository) {
        this.subjectInfoRepository = subjectInfoRepository;
        this.groupRepository = groupRepository;
        this.moduleRepository = moduleRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentRepository = studentRepository;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-students")
    public ResponseEntity<List<Student>> getAvailableStudents(@RequestParam Long professorId, @RequestParam Long semesterId) {
        List<StudentPerformanceInSubject> studentPerformanceInSubjects = new ArrayList<>(studentPerformanceInSubjectRepository
                .findByProfessorIdAndSemesterId(professorId, semesterId));

        List<Student> students = studentPerformanceInSubjects.stream()
                .map(StudentPerformanceInSubject::getStudent).distinct()
                .sorted((o1, o2) -> {
                    if (o1.getGroup().getId().equals(o2.getGroup().getId())) {
                        return o1.getSecondName().compareTo(o2.getSecondName());
                    } else {
                        return o1.getGroup().getTitle().compareTo(o2.getGroup().getTitle());
                    }
                })
                .collect(Collectors.toList());

        if (students.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-subjects")
    public ResponseEntity<List<Subject>> getAvailableSubjects(@RequestParam Long professorId, @RequestParam Long semesterId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>(subjectInfoRepository
                .findByProfessorIdAndSemesterId(professorId, semesterId));

        List<Subject> subjects = subjectInfos.stream()
                .map(SubjectInfo::getSubject).distinct()
                .sorted(Comparator.comparing(Subject::getTitle))
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-groups")
    public ResponseEntity<List<SubjectInfo>> getAvailableGroups(@RequestParam Long professorId, @RequestParam Long semesterId,
                                                                @RequestParam Long subjectId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>();
        if (subjectId == null) {
            subjectInfos.addAll(subjectInfoRepository
                    .findByProfessorIdAndSemesterId(professorId, semesterId).stream()
                    .sorted(Comparator.comparing(s -> s.getGroup().getTitle()))
                    .collect(Collectors.toList()));
        } else {
            subjectInfos.addAll(subjectInfoRepository
                    .findByProfessorIdAndSemesterIdAndSubjectId(professorId, semesterId, subjectId).stream()
                    .sorted(Comparator.comparing(s -> s.getGroup().getTitle()))
                    .collect(Collectors.toList()));
        }

/*        List<SubjectInfo> subjectInfosDistinct = new ArrayList<>();
        for (SubjectInfo subjectInfo : subjectInfos) {
            boolean isOk = true;
            for (SubjectInfo subjectInfo2 : subjectInfosDistinct) {
                if (subjectInfo.getGroup().getId().equals(subjectInfo2.getGroup().getId())) {
                    isOk = false;
                    break;
                }
            }
            if (isOk)
                subjectInfosDistinct.add(subjectInfo);
        }*/

        if (subjectInfos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjectInfos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/available-subjects-with-groups")
    public ResponseEntity<Map<String, List<SubjectInfo>>> getAvailableSubjectsWithGroups(@RequestParam Long professorId,
                                                                                         @RequestParam Long semesterId) {
        List<SubjectInfo> subjectInfos = new ArrayList<>(subjectInfoRepository
                .findByProfessorIdAndSemesterId(professorId, semesterId));

        List<Subject> subjects = subjectInfos.stream()
                .map(SubjectInfo::getSubject).distinct()
                .sorted(Comparator.comparing(Subject::getTitle))
                .collect(Collectors.toList());

        Map<String, List<SubjectInfo>> subjectsWithGroups = subjects.stream()
                .map(subject -> subject.getId())
                .collect(Collectors.toMap(String::valueOf,
                        s -> subjectInfoRepository
                                .findByProfessorIdAndSemesterIdAndSubjectId(professorId, semesterId, s).stream()
                                .sorted(Comparator.comparing(e -> e.getGroup().getTitle()))
                                .collect(Collectors.toList())));

        if (subjectsWithGroups.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(subjectsWithGroups, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/subjects-info/{id}")
    public ResponseEntity<SubjectInfo> getSubjectInfoById(@PathVariable("id") long id) {
        SubjectInfo subjectInfo = subjectInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found SubjectInfo with id = " + id));

        return new ResponseEntity<>(subjectInfo, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PostMapping("/subjects-info")
    public ResponseEntity<HttpStatus> createSubjectInfo(@RequestBody SubjectInfo subjectInfo) {
        Optional<SubjectInfo> _subjectInfo = subjectInfoRepository.findByGroupIdAndSemesterIdAndSubjectId(subjectInfo.getGroup().getId(),
                subjectInfo.getSemester().getId(), subjectInfo.getSubject().getId());

        SubjectInfo tempSubjectInfo;
        if (_subjectInfo.isPresent()) {
            tempSubjectInfo = _subjectInfo.get();
            if (subjectInfo.getLecturerId() != null)
                tempSubjectInfo.setLecturerId(subjectInfo.getLecturerId());
            if (subjectInfo.getSeminarsProfessor() != null)
                tempSubjectInfo.setSeminarsProfessor(subjectInfo.getSeminarsProfessor());
            tempSubjectInfo.setIsExam(subjectInfo.getIsExam());
            tempSubjectInfo.setIsDifferentiatedCredit(subjectInfo.getIsDifferentiatedCredit());
            subjectInfoRepository.save(tempSubjectInfo);
        } else {
            tempSubjectInfo = new SubjectInfo(subjectInfo.getGroup(), subjectInfo.getSubject(),
                    subjectInfo.getLecturerId(), subjectInfo.getSeminarsProfessor(), subjectInfo.getSemester(),
                    subjectInfo.getIsExam(), subjectInfo.getIsDifferentiatedCredit());
            SubjectInfo returnedSubjectInfo = subjectInfoRepository.save(tempSubjectInfo);

            Module module1;
            Module module2;
            Module module3;
            if (subjectInfo.getIsExam()) {
                module1 = moduleRepository.save(new Module(1, returnedSubjectInfo, 10, 0));
                module2 = moduleRepository.save(new Module(2, returnedSubjectInfo, 10, 0));
                module3 = moduleRepository.save(new Module(3, returnedSubjectInfo, 10, 0));
            } else {
                module1 = moduleRepository.save(new Module(1, returnedSubjectInfo, 20, 0));
                module2 = moduleRepository.save(new Module(2, returnedSubjectInfo, 20, 0));
                module3 = moduleRepository.save(new Module(3, returnedSubjectInfo, 20, 0));
            }

            List<StudentPerformanceInSubject> studentPerformanceInSubjects = studentRepository.findByGroupId(returnedSubjectInfo.getGroup().getId()).stream()
                    .map(s -> studentPerformanceInSubjectRepository.save(new StudentPerformanceInSubject(returnedSubjectInfo, s))).collect(Collectors.toList());

            studentPerformanceInSubjects.forEach(p ->
                    studentPerformanceInModuleRepository.save(new StudentPerformanceInModule(module1, p)));
            studentPerformanceInSubjects.forEach(p ->
                    studentPerformanceInModuleRepository.save(new StudentPerformanceInModule(module2, p)));
            studentPerformanceInSubjects.forEach(p ->
                    studentPerformanceInModuleRepository.save(new StudentPerformanceInModule(module3, p)));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/subjects-info/{id}")
    public ResponseEntity<HttpStatus> updateSubjectInfo(@PathVariable("id") long id, @RequestBody SubjectInfo subjectInfo) {
        SubjectInfo _subjectInfo = subjectInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found SubjectInfo with id = " + id));

        _subjectInfo.setGroup(subjectInfo.getGroup());
        _subjectInfo.setSubject(subjectInfo.getSubject());
        _subjectInfo.setLecturerId(subjectInfo.getLecturerId());
        _subjectInfo.setSeminarsProfessor(subjectInfo.getSeminarsProfessor());
        _subjectInfo.setSemester(subjectInfo.getSemester());
        _subjectInfo.setIsExam(subjectInfo.getIsExam());
        _subjectInfo.setIsDifferentiatedCredit(subjectInfo.getIsDifferentiatedCredit());
        subjectInfoRepository.save(_subjectInfo);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/change-group-in-subjects-info/{fromGroupId}/{toGroupId}")
    public ResponseEntity<HttpStatus> changeGroupInSubjectsInfo(@PathVariable("fromGroupId") long fromGroupId, @PathVariable("toGroupId") long toGroupId) {
        List<SubjectInfo> subjectInfos = subjectInfoRepository.findByGroupId(fromGroupId);
        Group group = groupRepository.findById(toGroupId).orElseThrow(() ->
                new ResourceNotFoundException("Not found Group with id = " + toGroupId));

        for (SubjectInfo subjectInfo : subjectInfos) {
            subjectInfo.setGroup(group);
            subjectInfoRepository.save(subjectInfo);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @DeleteMapping("/subjects-info/{id}")
    public ResponseEntity<HttpStatus> deleteSubjectInfo(@PathVariable("id") long id, @RequestParam Long professorId) {
        SubjectInfo _subjectInfo = subjectInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found SubjectInfo with id = " + id));

        if (_subjectInfo.getLecturerId() != null) {
            if (_subjectInfo.getSeminarsProfessor() != null) {
                if (_subjectInfo.getLecturerId().equals(professorId) && _subjectInfo.getSeminarsProfessor().getId().equals(professorId)) {
                    subjectInfoRepository.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else if (_subjectInfo.getLecturerId().equals(professorId)) {
                    _subjectInfo.setLecturerId(null);
                    subjectInfoRepository.save(_subjectInfo);
                } else if (_subjectInfo.getSeminarsProfessor().getId().equals(professorId)) {
                    _subjectInfo.setSeminarsProfessor(null);
                    subjectInfoRepository.save(_subjectInfo);
                }
            } else {
                if (_subjectInfo.getLecturerId().equals(professorId)) {
                    subjectInfoRepository.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
        } else {
            if (_subjectInfo.getSeminarsProfessor() != null) {
                if (_subjectInfo.getSeminarsProfessor().getId().equals(professorId)) {
                    subjectInfoRepository.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            } else {
                subjectInfoRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}