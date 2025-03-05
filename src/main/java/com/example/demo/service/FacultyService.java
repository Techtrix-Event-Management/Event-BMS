package com.example.demo.service;

import com.example.demo.JwtUtil;
import com.example.demo.Model.Faculty;
import com.example.demo.repo.FacultyRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepo facultyRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    

    
    
    // Add a new faculty
    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    // Get all faculty members
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    // Get a faculty member by ID
    public Optional<Faculty> getFacultyById(Long id) {
        return facultyRepository.findById(id);
    }

    // Update an existing faculty member
    public Faculty updateFaculty(Long id, Faculty updatedFaculty) {
        Optional<Faculty> existingFaculty = facultyRepository.findById(id);
        if (existingFaculty.isPresent()) {
            Faculty faculty = existingFaculty.get();

            // Update only the fields provided by the client
            if (updatedFaculty.getName() != null) {
                faculty.setName(updatedFaculty.getName());
            }
            if (updatedFaculty.getDepartment() != null) {
                faculty.setDepartment(updatedFaculty.getDepartment());
            }
            if (updatedFaculty.getEmail() != null) {
                faculty.setEmail(updatedFaculty.getEmail());
            }
            if (updatedFaculty.getEvents() != null) {
            	System.out.println(updatedFaculty.getEvents());
                faculty.setEvents(updatedFaculty.getEvents());
            }

            return facultyRepository.save(faculty);
        }
        throw new RuntimeException("Faculty not found with ID: " + id);
    }

 // In FacultyService.java
    public Faculty getFacultyByEmail(String email) {
        Optional<Faculty> faculty = facultyRepository.findByEmail(email);
        return faculty.orElse(null); // Return null if no faculty is found
    }


    // Delete a faculty member by ID
    public void deleteFaculty(Long id) {
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
        } else {
            throw new RuntimeException("Faculty not found with ID: " + id);
        }
    }
}
