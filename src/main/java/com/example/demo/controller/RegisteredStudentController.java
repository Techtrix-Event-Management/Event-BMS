package com.example.demo.controller;

import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import org.springframework.http.HttpHeaders;
//import com.itextpdf.kernel.pdf.*;
//import com.itextpdf.layout.Document;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.layout.element.Paragraph;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.element.Cell;
//import org.springframework.http.ContentDisposition;
//
//import java.io.ByteArrayOutputStream;
//import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Events;
import com.example.demo.Model.RegisteredStudent;
import com.example.demo.Model.Team;
import com.example.demo.Model.TeamMember;
import com.example.demo.repo.EventRepository;
import com.example.demo.repo.RegisteredStudentRepository;
import com.example.demo.repo.TeamRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.RegisteredStudentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



@RestController
@RequestMapping("/api/registered-students")
public class RegisteredStudentController {

    @Autowired
    private RegisteredStudentService registeredStudentService;
    
    @Autowired
    private RegisteredStudentRepository studentRepository;
    
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TeamRepository teamRepository;
    
    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(
            @RequestParam("name") String name,
            @RequestParam("college") String college,
            @RequestParam("number") String number,
            @RequestParam("email") String email,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "utrNumber", required = false) String utrNumber, // For online payments
            @RequestParam(value = "receiptNumber", required = false) String receiptNumber, // For cash payments
            @RequestParam("paymentImage") MultipartFile file, // File: Either screenshot or receipt image
            @RequestParam("eventId") Long eventId) { // Accept eventId

        try {
            // Fetch event
            Events event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));

            // Call service to handle registration logic
            registeredStudentService.registerStudent(
                    name, college, number, email, paymentMethod, utrNumber, receiptNumber, file, event);

            return ResponseEntity.ok("Registration Successful! Pending Faculty Approval.");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving registration");
        }
    }

    @PostMapping(value = "/register-team", consumes = "multipart/form-data")
    public ResponseEntity<Team> registerTeam(
        @RequestParam("teamName") String teamName,
        @RequestParam("leaderName") String leaderName,
        @RequestParam("leaderCollege") String leaderCollege,
        @RequestParam("leaderNumber") String leaderNumber,
        @RequestParam("leaderEmail") String leaderEmail,
        @RequestParam("paymentMethod") String paymentMethod,
        @RequestParam(value = "utrNumber", required = false) String utrNumber,
        @RequestParam(value = "receiptNumber", required = false) String receiptNumber,
        @RequestParam("eventId") Long eventId,
        @RequestParam("teamMembers") String teamMembersJson,
        @RequestParam(value = "paymentImage", required = false) MultipartFile paymentImage
    ) throws IOException {

        // Convert JSON string to List<TeamMember>
        ObjectMapper objectMapper = new ObjectMapper();
        List<TeamMember> teamMembers = objectMapper.readValue(teamMembersJson, new TypeReference<>() {});

        // Convert payment image to byte[]
        byte[] imageBytes = paymentImage != null ? paymentImage.getBytes() : null;

        // Save team
        Team savedTeam = registeredStudentService.registerTeam(
            teamName, leaderName, leaderCollege, leaderNumber, leaderEmail,
            paymentMethod, utrNumber, receiptNumber, eventId, imageBytes, teamMembers
        );

        return new ResponseEntity<>(savedTeam, HttpStatus.CREATED);
    }
    
    
    
}