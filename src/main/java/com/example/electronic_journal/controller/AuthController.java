package com.example.electronic_journal.controller;

import com.example.electronic_journal.model.Professor;
import com.example.electronic_journal.repository.ProfessorRepository;
import com.example.electronic_journal.repository.StudentRepository;
import com.example.electronic_journal.security.jwt.JwtUtils;
import com.example.electronic_journal.security.payload.JwtResponse;
import com.example.electronic_journal.security.payload.LoginRequest;
import com.example.electronic_journal.security.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    AuthenticationManager authenticationManager;
    ProfessorRepository professorRepository;
    StudentRepository studentRepository;
    PasswordEncoder encoder;
    JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, ProfessorRepository professorRepository,
                          StudentRepository studentRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername().equals("admin")) {
            Optional<Professor> professor = professorRepository.findByUsername(loginRequest.getUsername());
            if (professor.isPresent()) {
                if (professor.get().getPassword().equals("123456")) {
                    Professor changedPasswordProfessor = professor.get();
                    changedPasswordProfessor.setPassword(encoder.encode("123456"));
                    professorRepository.save(changedPasswordProfessor);
                }
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()).get(0);

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), role));
    }
}
