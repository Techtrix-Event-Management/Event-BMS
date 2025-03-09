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
@CrossOrigin( allowCredentials = "true")
public class FacultyController {

	@Autowired
	private JwtUtil jwtUtil;
    @Autowired
    private FacultyService facultyService;

    

   
    
//    @PostMapping
//    public Faculty addFaculty(@RequestBody Faculty faculty) {
//        return facultyService.addFaculty(faculty);
//    }
//
//    @GetMapping
//    public List<Faculty> getAllFaculty() {
//        return facultyService.getAllFaculty();
//    }
//
//    @GetMapping("/{id}")
//    public Optional<Faculty> getFacultyById(@PathVariable Long id) {
//        return facultyService.getFacultyById(id);
//    }
//
//    @PutMapping("/{id}")
//    public Faculty updateFaculty(@PathVariable Long id, @RequestBody Faculty updatedFaculty) {
//        return facultyService.updateFaculty(id, updatedFaculty);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteFaculty(@PathVariable Long id) {
//        facultyService.deleteFaculty(id);
//    }
    
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