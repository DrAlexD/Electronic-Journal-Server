package com.example.electronic_journal;

/*import com.example.electronic_journal.model.*;*/

import com.example.electronic_journal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*import java.util.Calendar;
import java.util.Date;*/


@Component
public class DatabaseLoader implements CommandLineRunner {
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectInfoRepository subjectInfoRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final EventRepository eventRepository;
    private final StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository;
    private final StudentPerformanceInModuleRepository studentPerformanceInModuleRepository;
    private final StudentLessonRepository studentLessonRepository;
    private final StudentEventRepository studentEventRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public DatabaseLoader(ProfessorRepository professorRepository, StudentRepository studentRepository, GroupRepository groupRepository,
                          SubjectRepository subjectRepository, SemesterRepository semesterRepository, SubjectInfoRepository subjectInfoRepository,
                          ModuleRepository moduleRepository, LessonRepository lessonRepository, EventRepository eventRepository,
                          StudentPerformanceInSubjectRepository studentPerformanceInSubjectRepository,
                          StudentPerformanceInModuleRepository studentPerformanceInModuleRepository,
                          StudentLessonRepository studentLessonRepository, StudentEventRepository studentEventRepository,
                          PasswordEncoder encoder
    ) {
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
        this.subjectInfoRepository = subjectInfoRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.eventRepository = eventRepository;
        this.studentPerformanceInSubjectRepository = studentPerformanceInSubjectRepository;
        this.studentPerformanceInModuleRepository = studentPerformanceInModuleRepository;
        this.studentLessonRepository = studentLessonRepository;
        this.studentEventRepository = studentEventRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... strings) throws Exception {
        /*
        Professor professor1 = new Professor("Игорь", "Вишняков", "qwerty", encoder.encode("123456"));
        professor1.setRole(ERole.ROLE_ADMIN);
        Professor professor2 = new Professor("Сергей", "Скоробогатов", "СергейСкоробогатов", encoder.encode("1234567"));
        Professor professor3 = new Professor("Анна", "Домрачева", "АннаДомрачева", encoder.encode("1234568"));

        professorRepository.save(professor1);
        professorRepository.save(professor2);
        professorRepository.save(professor3);


        Semester semester1 = new Semester(2018, true);
        Semester semester2 = new Semester(2018, false);
        Semester semester3 = new Semester(2019, true);

        semesterRepository.save(semester1);
        semesterRepository.save(semester2);
        semesterRepository.save(semester3);

        Subject subject1 = new Subject("Математический анализ");
        Subject subject2 = new Subject("Моделирование");
        Subject subject3 = new Subject("Оптимизационные методы");

        subjectRepository.save(subject1);
        subjectRepository.save(subject2);
        subjectRepository.save(subject3);

        Group group1 = new Group("ИУ9-81");
        Group group2 = new Group("ИУ9-71");
        Group group3 = new Group("ИУ9-61");

        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(group3);

        Student student11 = new Student("Александр", "Другаков", group1, "АлександрДругаков", encoder.encode("123456"));
        Student student12 = new Student("Артем", "Егорычев", group1, "АртемЕгорычев", encoder.encode("123456"));
        Student student13 = new Student("Иван", "Филоненко", group1, "ИванФилоненко", encoder.encode("123456"));

        studentRepository.save(student11);
        studentRepository.save(student12);
        studentRepository.save(student13);

        Student student21 = new Student("Александр2", "Другаков2", group2, "Александр2Другаков2", encoder.encode("123456"));
        Student student22 = new Student("Артем2", "Егорычев2", group2, "Артем2Егорычев2", encoder.encode("123456"));
        Student student23 = new Student("Иван2", "Филоненко2", group2, "Иван2Филоненко2", encoder.encode("123456"));

        studentRepository.save(student21);
        studentRepository.save(student22);
        studentRepository.save(student23);

        Student student31 = new Student("Александр3", "Другаков3", group3, "Александр3Другаков3", encoder.encode("123456"));
        Student student32 = new Student("Артем3", "Егорычев3", group3, "Артем3Егорычев3", encoder.encode("123456"));
        Student student33 = new Student("Иван3", "Филоненко3", group3, "Иван3Филоненко3", encoder.encode("123456"));

        studentRepository.save(student31);
        studentRepository.save(student32);
        studentRepository.save(student33);

        SubjectInfo subjectInfo1 = new SubjectInfo(group3, subject1, professor2.getId(), professor1, semester3,
                true, false);
        SubjectInfo subjectInfo2 = new SubjectInfo(group1, subject2, professor1.getId(), professor1, semester3,
                false, false);
        SubjectInfo subjectInfo3 = new SubjectInfo(group2, subject3, professor2.getId(), professor3, semester3,
                false, true);

        subjectInfoRepository.save(subjectInfo1);
        subjectInfoRepository.save(subjectInfo2);
        subjectInfoRepository.save(subjectInfo3);

        Module module11 = new Module(1, subjectInfo1, 15, 30);
        Module module21 = new Module(2, subjectInfo1, 16, 31);
        Module module31 = new Module(3, subjectInfo1, 17, 32);
        Module module12 = new Module(1, subjectInfo2, 18, 33);
        Module module22 = new Module(2, subjectInfo2, 19, 34);
        Module module32 = new Module(3, subjectInfo2, 20, 35);
        Module module13 = new Module(1, subjectInfo3, 21, 36);
        Module module23 = new Module(2, subjectInfo3, 22, 37);
        Module module33 = new Module(3, subjectInfo3, 23, 38);

        moduleRepository.save(module11);
        moduleRepository.save(module21);
        moduleRepository.save(module31);
        moduleRepository.save(module12);
        moduleRepository.save(module22);
        moduleRepository.save(module32);
        moduleRepository.save(module13);
        moduleRepository.save(module23);
        moduleRepository.save(module33);

        Lesson lesson111 = new Lesson(module11, new Date(120, Calendar.OCTOBER, 1), true, 1);
        Lesson lesson211 = new Lesson(module11, new Date(120, Calendar.OCTOBER, 1), true, 2);
        Lesson lesson121 = new Lesson(module21, new Date(120, Calendar.OCTOBER, 11), true, 3);
        Lesson lesson221 = new Lesson(module21, new Date(120, Calendar.OCTOBER, 11), true, 4);
        Lesson lesson131 = new Lesson(module31, new Date(120, Calendar.OCTOBER, 21), true, 5);
        Lesson lesson231 = new Lesson(module31, new Date(120, Calendar.OCTOBER, 21), true, 6);
        Lesson lesson112 = new Lesson(module12, new Date(120, Calendar.OCTOBER, 2), true, 7);
        Lesson lesson212 = new Lesson(module12, new Date(120, Calendar.OCTOBER, 2), true, 8);
        Lesson lesson122 = new Lesson(module22, new Date(120, Calendar.OCTOBER, 12), true, 9);
        Lesson lesson222 = new Lesson(module22, new Date(120, Calendar.OCTOBER, 12), true, 10);
        Lesson lesson132 = new Lesson(module32, new Date(120, Calendar.OCTOBER, 22), true, 11);
        Lesson lesson232 = new Lesson(module32, new Date(120, Calendar.OCTOBER, 22), true, 12);
        Lesson lesson113 = new Lesson(module13, new Date(120, Calendar.OCTOBER, 3), true, 13);
        Lesson lesson213 = new Lesson(module13, new Date(120, Calendar.OCTOBER, 3), true, 14);
        Lesson lesson123 = new Lesson(module23, new Date(120, Calendar.OCTOBER, 13), true, 15);
        Lesson lesson223 = new Lesson(module23, new Date(120, Calendar.OCTOBER, 13), true, 16);
        Lesson lesson133 = new Lesson(module33, new Date(120, Calendar.OCTOBER, 23), true, 17);
        Lesson lesson233 = new Lesson(module33, new Date(120, Calendar.OCTOBER, 23), true, 18);

        lessonRepository.save(lesson111);
        lessonRepository.save(lesson211);
        lessonRepository.save(lesson121);
        lessonRepository.save(lesson221);
        lessonRepository.save(lesson131);
        lessonRepository.save(lesson231);
        lessonRepository.save(lesson112);
        lessonRepository.save(lesson212);
        lessonRepository.save(lesson122);
        lessonRepository.save(lesson222);
        lessonRepository.save(lesson132);
        lessonRepository.save(lesson232);
        lessonRepository.save(lesson113);
        lessonRepository.save(lesson213);
        lessonRepository.save(lesson123);
        lessonRepository.save(lesson223);
        lessonRepository.save(lesson133);
        lessonRepository.save(lesson233);

        Event event11 = new Event(module11, 0, 1, new Date(120, Calendar.OCTOBER, 1),
                new Date(120, Calendar.OCTOBER, 9), 15, 30);
        Event event21 = new Event(module21, 1, 2, new Date(120, Calendar.OCTOBER, 2),
                new Date(120, Calendar.OCTOBER, 8), 16, 31);
        Event event31 = new Event(module31, 2, 3, new Date(120, Calendar.OCTOBER, 3),
                new Date(120, Calendar.OCTOBER, 7), 17, 32);
        Event event12 = new Event(module12, 2, 1, new Date(120, Calendar.OCTOBER, 4),
                new Date(120, Calendar.OCTOBER, 6), 18, 33);
        Event event22 = new Event(module22, 1, 2, new Date(120, Calendar.OCTOBER, 5),
                new Date(120, Calendar.OCTOBER, 5), 19, 34);
        Event event32 = new Event(module32, 0, 3, new Date(120, Calendar.OCTOBER, 6),
                new Date(120, Calendar.OCTOBER, 4), 20, 35);
        Event event13 = new Event(module13, 1, 1, new Date(120, Calendar.OCTOBER, 7),
                new Date(120, Calendar.OCTOBER, 3), 21, 36);
        Event event23 = new Event(module23, 2, 2, new Date(120, Calendar.OCTOBER, 8),
                new Date(120, Calendar.OCTOBER, 2), 22, 37);
        Event event33 = new Event(module33, 0, 3, new Date(120, Calendar.OCTOBER, 9),
                new Date(120, Calendar.OCTOBER, 1), 23, 38);

        eventRepository.save(event11);
        eventRepository.save(event21);
        eventRepository.save(event31);
        eventRepository.save(event12);
        eventRepository.save(event22);
        eventRepository.save(event32);
        eventRepository.save(event13);
        eventRepository.save(event23);
        eventRepository.save(event33);

        StudentPerformanceInSubject studentPerformanceInSubject1 = new StudentPerformanceInSubject(subjectInfo2, student11);
        StudentPerformanceInSubject studentPerformanceInSubject2 = new StudentPerformanceInSubject(subjectInfo2, student13);
        StudentPerformanceInSubject studentPerformanceInSubject3 = new StudentPerformanceInSubject(subjectInfo3, student22);

        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject1);
        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject2);
        studentPerformanceInSubjectRepository.save(studentPerformanceInSubject3);

        StudentPerformanceInModule studentPerformanceInModule1 = new StudentPerformanceInModule(module12,
                studentPerformanceInSubject1);
        StudentPerformanceInModule studentPerformanceInModule2 = new StudentPerformanceInModule(module22,
                studentPerformanceInSubject1);
        StudentPerformanceInModule studentPerformanceInModule3 = new StudentPerformanceInModule(module12,
                studentPerformanceInSubject2);
        StudentPerformanceInModule studentPerformanceInModule4 = new StudentPerformanceInModule(module22,
                studentPerformanceInSubject2);
        StudentPerformanceInModule studentPerformanceInModule5 = new StudentPerformanceInModule(module13,
                studentPerformanceInSubject3);
        StudentPerformanceInModule studentPerformanceInModule6 = new StudentPerformanceInModule(module23,
                studentPerformanceInSubject3);

        studentPerformanceInModuleRepository.save(studentPerformanceInModule1);
        studentPerformanceInModuleRepository.save(studentPerformanceInModule2);
        studentPerformanceInModuleRepository.save(studentPerformanceInModule3);
        studentPerformanceInModuleRepository.save(studentPerformanceInModule4);
        studentPerformanceInModuleRepository.save(studentPerformanceInModule5);
        studentPerformanceInModuleRepository.save(studentPerformanceInModule6);

        StudentLesson studentLesson1 = new StudentLesson(studentPerformanceInModule1, lesson112, true);
        StudentLesson studentLesson2 = new StudentLesson(studentPerformanceInModule1, lesson212, false);
        StudentLesson studentLesson3 = new StudentLesson(studentPerformanceInModule2, lesson222, false);
        StudentLesson studentLesson4 = new StudentLesson(studentPerformanceInModule2, lesson222, true);
        StudentLesson studentLesson5 = new StudentLesson(studentPerformanceInModule3, lesson212, false);
        StudentLesson studentLesson6 = new StudentLesson(studentPerformanceInModule3, lesson212, true);

        studentLessonRepository.save(studentLesson1);
        studentLessonRepository.save(studentLesson2);
        studentLessonRepository.save(studentLesson3);
        studentLessonRepository.save(studentLesson4);
        studentLessonRepository.save(studentLesson5);
        studentLessonRepository.save(studentLesson6);

        StudentEvent studentEvent1 = new StudentEvent(1, studentPerformanceInModule1, event12,
                true, 5);
        StudentEvent studentEvent2 = new StudentEvent(1, studentPerformanceInModule1, event12,
                false, 4);
        StudentEvent studentEvent3 = new StudentEvent(1, studentPerformanceInModule2, event22,
                false, 2);
        StudentEvent studentEvent4 = new StudentEvent(1, studentPerformanceInModule2, event22,
                true, 10);
        StudentEvent studentEvent5 = new StudentEvent(1, studentPerformanceInModule3, event12,
                false, 12);
        StudentEvent studentEvent6 = new StudentEvent(1, studentPerformanceInModule3, event12,
                true, 3);

        studentEventRepository.save(studentEvent1);
        studentEventRepository.save(studentEvent2);
        studentEventRepository.save(studentEvent3);
        studentEventRepository.save(studentEvent4);
        studentEventRepository.save(studentEvent5);
        studentEventRepository.save(studentEvent6);
        */
    }
}