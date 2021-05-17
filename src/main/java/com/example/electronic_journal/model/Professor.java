package com.example.electronic_journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String secondName;

    @Enumerated(EnumType.STRING)
    private ERole role;

    @JsonIgnore
    @OneToMany(mappedBy = "seminarian", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<SubjectInfo> subjectInfos;

    public Professor() {
    }

    public Professor(String firstName, String secondName, String username, String password) {
        super();
        this.firstName = firstName;
        this.secondName = secondName;
        this.username = username;
        this.password = password;
        this.role = ERole.ROLE_PROFESSOR;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }

    public Set<SubjectInfo> getSubjectInfos() {
        return subjectInfos;
    }

    public void setSubjectInfos(Set<SubjectInfo> subjectInfos) {
        this.subjectInfos = subjectInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        return Objects.equals(id, professor.id) && Objects.equals(username, professor.username) && Objects.equals(password, professor.password) && Objects.equals(firstName, professor.firstName) && Objects.equals(secondName, professor.secondName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, firstName, secondName);
    }

    @Override
    public String toString() {
        return "Professor{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", role=" + role +
                '}';
    }
}
