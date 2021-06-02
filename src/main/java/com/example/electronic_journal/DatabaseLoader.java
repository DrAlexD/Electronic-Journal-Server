/*
package com.example.electronic_journal;

import com.example.electronic_journal.model.ERole;
import com.example.electronic_journal.model.Professor;
import com.example.electronic_journal.model.Semester;
import com.example.electronic_journal.repository.ProfessorRepository;
import com.example.electronic_journal.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private final ProfessorRepository professorRepository;
    private final SemesterRepository semesterRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public DatabaseLoader(ProfessorRepository professorRepository, SemesterRepository semesterRepository,
                          PasswordEncoder encoder
    ) {
        this.professorRepository = professorRepository;
        this.semesterRepository = semesterRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... strings) throws Exception {
        Professor professor = new Professor("Игорь", "Вишняков", "admin", encoder.encode("123456"));
        professor.setRole(ERole.ROLE_ADMIN);
        professorRepository.save(professor);

        Semester semester = new Semester(2021, true);
        semesterRepository.save(semester);
    }
}
*/
