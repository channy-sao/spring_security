package org.example.spring_security_demo.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class StudentController {

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public String getStudents(){
        return "Get all student";
    }

}
