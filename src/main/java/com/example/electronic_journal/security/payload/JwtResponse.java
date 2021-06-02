package com.example.electronic_journal.security.payload;

public class JwtResponse {
    private final String token;
    private final Long id;
    private final String role;

    public JwtResponse(String token, Long id, String role) {
        this.token = token;
        this.id = id;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
