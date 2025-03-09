package com.example.demo.controller;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.JwtUtil;
import com.example.demo.Model.Faculty;
import com.example.demo.Model.RegisteredStudent;
import com.example.demo.Model.Team;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
	    String email = requestBody.get("email");
	    String password = requestBody.get("password");

	    if (email == null || password == null) {
	        return ResponseEntity.badRequest().body("Email and password are required.");
	    }

	    try {
	        // Assume authService.signup returns a user object with role
	        Faculty faculty = authService.signup(email, password);

	        // Ensure role is assigned (e.g., defaulting to ADMIN for now)
	        List<String> roles = List.of("ADMIN"); // Change as per your role assignment logic

	        // Generate token with roles
	        String token = jwtUtil.generateToken(email, roles);

	        // Set token as HttpOnly cookie
	        Cookie cookie = new Cookie("auth_token", token);
	        cookie.setHttpOnly(true);
	   	cookie.setSecure(false);
	        cookie.setPath("/");
	        cookie.setMaxAge(24 * 60 * 60);

	        response.addCookie(cookie);
	        return ResponseEntity.ok("Signup successful");
	    } catch (ResponseStatusException e) {
	        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
	    }
	}


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
    	String email = requestBody.get("email");
        String password = requestBody.get("password");
        try {
            String token = authService.login(email, password);

            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);
            return ResponseEntity.ok("Login successful");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Optional<Cookie> authCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("auth_token"))
                .findFirst();

        if (authCookie.isEmpty() || !jwtUtil.validateToken(authCookie.get().getValue())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = jwtUtil.extractUsername(authCookie.get().getValue());
        return ResponseEntity.ok(Map.of("email", email));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }


    
    @GetMapping("/{id}/registered-students")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RegisteredStudent> getAllRegisteredStudentsByEventId(@PathVariable Long id) {
        return authService.getAllRegisteredStudentsByEventId(id)
            .stream()
            .map(student -> {
                if (student.getPaymentImage() != null) {
                    student.setPaymentImage(Base64.getEncoder().encode(student.getPaymentImage()));
                }
                return student;
            })
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}/teams")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Team> getRegisteredTeamsByEventId(@PathVariable Long id) {
        return authService.getAllTeamsByEventId(id);
    }

    @GetMapping("/{id}/get-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisteredStudent> getRegisteredStudentById(@PathVariable Long id) {
        return authService.getRegisteredStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchStudents(@RequestParam String name) {
        List<RegisteredStudent> students = authService.searchStudentsByName(name);

        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No students found"));
        }

        return ResponseEntity.ok(students);
    }

    @GetMapping("/teams/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchTeams(@RequestParam String keyword) {
        List<Team> teams = authService.searchTeamsByKeyword(keyword);

        if (teams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No teams found"));
        }

        return ResponseEntity.ok(teams);
    }



}
