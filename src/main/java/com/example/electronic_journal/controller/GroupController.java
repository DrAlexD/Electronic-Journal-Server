package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Group;
import com.example.electronic_journal.repository.GroupRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GroupController {

    private final GroupRepository groupRepository;

    public GroupController(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = new ArrayList<>(groupRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));

        if (groups.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/groups/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable("id") long id) {
        Group group = groupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Group with id = " + id));

        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/groups")
    public ResponseEntity<HttpStatus> createGroup(@RequestBody Group group) {
        if (groupRepository.existsByTitle(group.getTitle())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        groupRepository.save(new Group(group.getTitle()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/groups/{id}")
    public ResponseEntity<HttpStatus> updateGroup(@PathVariable("id") long id, @RequestBody Group group) {
        Group _group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Group with id = " + id));

        _group.setTitle(group.getTitle());
        groupRepository.save(_group);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable("id") long id) {
        groupRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}