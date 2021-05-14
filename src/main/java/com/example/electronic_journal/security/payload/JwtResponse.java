package com.example.electronic_journal.security.payload;

public class JwtResponse {
    private final String token;
    private final Long id;
    private final Boolean isProfessor;

    public JwtResponse(String token, Long id, Boolean isProfessor) {
        this.token = token;
        this.id = id;
        this.isProfessor = isProfessor;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public Boolean getIsProfessor() {
        return isProfessor;
    }
}
