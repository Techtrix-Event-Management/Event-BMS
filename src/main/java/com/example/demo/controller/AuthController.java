package com.example.demo.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@CrossOrigin(allowCredentials="true")

public class AuthController {

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthService authService;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
	
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
	        cookie.setSecure(true);
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
            cookie.setSecure(true);
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
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }


    
   
   

 // 1. Display all registered students (Solo)
    @GetMapping("/{id}/students/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<RegisteredStudent>> getAllRegisteredStudents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<RegisteredStudent> studentPagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RegisteredStudent> studentsPage = authService.getAllRegisteredStudentsByEventId(id, pageable);

        return ResponseEntity.ok(formatStudentPage(studentsPage, studentPagedResourcesAssembler));
    }

    // 2. Display only verified students (Solo)
    @GetMapping("/{id}/students/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<RegisteredStudent>> getVerifiedStudents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<RegisteredStudent> studentPagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RegisteredStudent> studentsPage = authService.getStudentsByStatus(id, "verified", pageable);

        return ResponseEntity.ok(formatStudentPage(studentsPage, studentPagedResourcesAssembler));
    }

    // 3. Display only not verified students (Solo)
    @GetMapping("/{id}/students/not-verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<RegisteredStudent>> getNotVerifiedStudents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<RegisteredStudent> studentPagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RegisteredStudent> studentsPage = authService.getStudentsByStatus(id, "pending", pageable);

        return ResponseEntity.ok(formatStudentPage(studentsPage, studentPagedResourcesAssembler));
    }

    // 4. Display all teams
    @GetMapping("/{id}/teams/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<Team>> getAllRegisteredTeams(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Team> teamPagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Team> teamsPage = authService.getAllTeamsByEventId(id, pageable);

        return ResponseEntity.ok(formatTeamPage(teamsPage, teamPagedResourcesAssembler));
    }

    // 5. Display only verified teams
    @GetMapping("/{id}/teams/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<Team>> getVerifiedTeams(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Team> teamPagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Team> teamsPage = authService.getTeamsByStatus(id, "verified", pageable);

        return ResponseEntity.ok(formatTeamPage(teamsPage, teamPagedResourcesAssembler));
    }

    // 6. Display only not verified teams
    @GetMapping("/{id}/teams/not-verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<Team>> getNotVerifiedTeams(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Team> teamPagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Team> teamsPage = authService.getTeamsByStatus(id, "pending", pageable);

        return ResponseEntity.ok(formatTeamPage(teamsPage, teamPagedResourcesAssembler));
    }

    // Helper method to format students with date-time
    private PagedModel<RegisteredStudent> formatStudentPage(Page<RegisteredStudent> studentsPage, 
            PagedResourcesAssembler<RegisteredStudent> assembler) {
			return PagedModel.of(studentsPage.map(student -> {
			student.setFormattedRegistrationDate(student.getFormattedRegistrationDate());
			return student;
			}).getContent(), assembler.toModel(studentsPage).getMetadata());
	}



    // Helper method to format teams with date-time
    private PagedModel<Team> formatTeamPage(Page<Team> teamsPage, 
            PagedResourcesAssembler<Team> assembler) {
			return PagedModel.of(teamsPage.map(team -> {
			team.setFormattedRegistrationDate(team.getFormattedRegistrationDate());
			return team;
			}).getContent(), assembler.toModel(teamsPage).getMetadata());
			}


    
    
    @GetMapping("/{id}/students/all/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegisteredStudent>> downloadAllRegisteredStudents(@PathVariable Long id) {
        List<RegisteredStudent> students = authService.getAllRegisteredStudentsByEventId(id)
                .stream().map(this::formatStudentDate).collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/students/verified/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegisteredStudent>> downloadVerifiedStudents(@PathVariable Long id) {
        List<RegisteredStudent> students = authService.getStudentsByStatus(id, "verified")
                .stream().map(this::formatStudentDate).collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/teams/all/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Team>> downloadAllRegisteredTeams(@PathVariable Long id) {
        List<Team> teams = authService.getAllTeamsByEventId(id)
                .stream().map(this::formatTeamDate).collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}/teams/verified/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Team>> downloadVerifiedTeams(@PathVariable Long id) {
        List<Team> teams = authService.getTeamsByStatus(id, "verified")
                .stream().map(this::formatTeamDate).collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    private RegisteredStudent formatStudentDate(RegisteredStudent student) {
        student.setFormattedRegistrationDate(student.getFormattedRegistrationDate());
        return student;
    }

    private Team formatTeamDate(Team team) {
        team.setFormattedRegistrationDate(team.getFormattedRegistrationDate());
        return team;
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
