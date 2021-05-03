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

    @GetMapping("/groups/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable("id") long id) {
        Group group = groupRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Group with id = " + id));

        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping("/groups")
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        Group _group = groupRepository
                .save(new Group(group.getTitle()));
        return new ResponseEntity<>(_group, HttpStatus.CREATED);
    }

    @PutMapping("/groups/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable("id") long id, @RequestBody Group group) {
        Group _group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Group with id = " + id));

        _group.setTitle(group.getTitle());

        return new ResponseEntity<>(groupRepository.save(_group), HttpStatus.OK);
    }

    @DeleteMapping("/groups/{id}")
    public ResponseEntity<HttpStatus> deleteGroup(@PathVariable("id") long id) {
        groupRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}