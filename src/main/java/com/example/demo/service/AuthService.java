package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.JwtUtil;
import com.example.demo.Model.Faculty;
import com.example.demo.Model.RegisteredStudent;
import com.example.demo.Model.Team;
import com.example.demo.repo.FacultyRepo;
import com.example.demo.repo.RegisteredStudentRepository;
import com.example.demo.repo.TeamRepository;

@Service
public class AuthService {

	@Autowired
	private FacultyRepo facultyRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	RegisteredStudentRepository registeredStudentRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	public Faculty signup(String email, String password) {
	    // Check if email already exists
	    if (facultyRepo.findByEmail(email).isPresent()) {
	        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
	    }

	    // Create new Faculty entity with default values
	    Faculty newFaculty = new Faculty();
	    String encodedPassword = passwordEncoder.encode(password);
	    
	    newFaculty.setEmail(email);
	    newFaculty.setPassword(encodedPassword);
	    newFaculty.setName("Default Name"); // Set default name
	    newFaculty.setDepartment("Default Department"); // Set default department
	    newFaculty.setRoles(List.of("ADMIN")); // Assign ADMIN role

	    System.out.println("Encoded Password: " + encodedPassword); 
	    return facultyRepo.save(newFaculty);
	}


	public String login(String email, String password) {
	    Faculty admin = facultyRepo.findByEmail(email)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

	    System.out.println("Raw Password: " + password);
	    System.out.println("Stored Hashed Password: " + admin.getPassword());

	    if (!passwordEncoder.matches(password, admin.getPassword())) {
	        System.out.println("Password Mismatch!");
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
	    }

	    // Ensure the token contains roles
	    return jwtUtil.generateToken(admin.getEmail(), admin.getRoles());
	}


    
    public List<RegisteredStudent> getAllRegisteredStudentsByEventId(Long eventId) {
        return registeredStudentRepository.findByEventId(eventId);
    }

    public List<Team> getAllTeamsByEventId(Long eventId) {
        return teamRepository.findByEventIdWithMembers(eventId);
    }
    
    public List<RegisteredStudent> searchStudentsByName(String name) {
        return registeredStudentRepository.findByNameContainingIgnoreCase(name);
    }
    

    public List<Team> searchTeamsByKeyword(String keyword) {
        return teamRepository.findByTeamNameContainingIgnoreCaseOrLeaderNameContainingIgnoreCase(keyword, keyword);
    }
    public Optional<RegisteredStudent> getRegisteredStudentById(Long id) {
        return registeredStudentRepository.findById(id);
    }

    public void deleteRegisteredStudent(Long id) {
        registeredStudentRepository.deleteById(id);
    }

}
