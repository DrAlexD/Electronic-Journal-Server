package com.example.electronic_journal.controller;

import com.example.electronic_journal.exception.ResourceNotFoundException;
import com.example.electronic_journal.model.Module;
import com.example.electronic_journal.repository.ModuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ModuleController {

    private final ModuleRepository moduleRepository;

    public ModuleController(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/modules")
    public ResponseEntity<Map<String, Module>> getModules(@RequestParam Long subjectInfoId) {
        List<Module> modules = new ArrayList<>(
                moduleRepository.findBySubjectInfoId(subjectInfoId));

        Map<String, Module> modulesByModuleNumber = modules.stream()
                .collect(Collectors.toMap(m -> String.valueOf(m.getModuleNumber()), m -> m));

        if (modulesByModuleNumber.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(modulesByModuleNumber, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/modules/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable("id") long id) {
        Module module = moduleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found Module with id = " + id));

        return new ResponseEntity<>(module, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @PutMapping("/modules/{id}")
    public ResponseEntity<HttpStatus> updateModule(@PathVariable("id") long id, @RequestBody Module module) {
        Module _module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Module with id = " + id));

        _module.setModuleNumber(module.getModuleNumber());
        _module.setSubjectInfo(module.getSubjectInfo());
        _module.setMinPoints(module.getMinPoints());
        _module.setMaxPoints(module.getMaxPoints());
        moduleRepository.save(_module);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}