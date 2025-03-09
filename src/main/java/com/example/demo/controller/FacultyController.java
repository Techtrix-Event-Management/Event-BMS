package com.example.demo.controller;

import com.example.demo.JwtUtil;
import com.example.demo.Model.Faculty;
import com.example.demo.service.FacultyService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class FacultyController {

	@Autowired
	private JwtUtil jwtUtil;
    @Autowired
    private FacultyService facultyService;
    
 // Add this method to your FacultyController
    @GetMapping("/getFacultyByEmail")
    public ResponseEntity<Faculty> getFacultyByEmail(@RequestParam String email) {
        Faculty faculty = facultyService.getFacultyByEmail(email);
        if (faculty != null) {
            return ResponseEntity.ok(faculty);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

}
