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
    
    
    
   
    
//    @PutMapping("/{id}/verify")
//    public ResponseEntity<?> verifyStudent(@PathVariable Long id) {
//        Optional<RegisteredStudent> studentOpt = studentRepository.findById(id);
//        if (studentOpt.isPresent()) {
//            RegisteredStudent student = studentOpt.get();
//            student.setStatus("verified");
//            studentRepository.save(student);
//
//            // Send verification email
//            String subject = "Registration Successful";
//            String body = "Congratulations! Your registration has been successfully verified. "
//                        + "You can now participate in the event.";
//            emailService.sendEmailToStudent(id, subject, body);
//
//            return ResponseEntity.ok("Student status updated to verified and email sent.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
//        }
//    }
    
//    @PutMapping("teams/{id}/verify")
//    public ResponseEntity<?> verifyTeam(@PathVariable Long id) {
//        Optional<Team> optionalTeam = teamRepository.findById(id);
//
//        if (optionalTeam.isPresent()) {
//            Team team = optionalTeam.get();
//            team.setStatus("verified");
//            teamRepository.save(team);
//
//            // Send email confirmation
//            String subject = "Team Registration Verified";
//            String body = "Congratulations! Your team '" + team.getTeamName() + "' has been successfully verified.";
//
//            emailService.sendEmailToTeam(id, subject, body);
//
//            return ResponseEntity.ok("✅ Team verified and email sent.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Team not found.");
//        }
//    }

//    @GetMapping("/download")
//    public ResponseEntity<byte[]> downloadStudents(
//            @RequestParam Long eventId,
//            @RequestParam boolean isGroup,
//            @RequestParam(required = false) Boolean verifiedOnly) {
//        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
//            PdfDocument pdfDocument = new PdfDocument(writer);
//            Document document = new Document(pdfDocument);
//
//            document.add(new Paragraph("Event Participants List").setBold().setFontSize(18));
//
//            if (!isGroup) {
//                List<RegisteredStudent> students = registeredStudentService.getAllRegisteredStudentsByEventId(eventId);
//                if (verifiedOnly != null && verifiedOnly) {
//                    students = students.stream()
//                            .filter(student -> "verified".equalsIgnoreCase(student.getStatus()))
//                            .collect(Collectors.toList());
//                }
//
//                if (students.isEmpty()) {
//                    document.add(new Paragraph("No solo participants found."));
//                } else {
//                    document.add(new Paragraph("Solo Participants:").setBold());
//                    Table table = new Table(new float[]{3, 3, 3, 3, 3, 3});
//                    table.addCell("Name").setBold();
//                    table.addCell("College").setBold();
//                    table.addCell("Email").setBold();
//                    table.addCell("Phone").setBold();
//                    table.addCell("Payment Method").setBold();
//                    table.addCell("Status").setBold();
//                    
//                    for (RegisteredStudent student : students) {
//                        table.addCell(student.getName());
//                        table.addCell(student.getCollege());
//                        table.addCell(student.getEmail());
//                        table.addCell(student.getNumber());
//                        table.addCell(student.getPaymentMethod());
//                        table.addCell(student.getStatus());
//                    }
//                    document.add(table);
//                }
//            } else {
//                List<Team> teams = registeredStudentService.getAllTeamsByEventId(eventId);
//                if (verifiedOnly != null && verifiedOnly) {
//                    teams = teams.stream()
//                            .filter(team -> "verified".equalsIgnoreCase(team.getStatus()))
//                            .collect(Collectors.toList());
//                }
//
//                if (teams.isEmpty()) {
//                    document.add(new Paragraph("No group participants found."));
//                } else {
//                    document.add(new Paragraph("Group Participants:").setBold());
//                    for (Team team : teams) {
//                        document.add(new Paragraph("Team: " + team.getTeamName()).setBold());
//                        Table table = new Table(new float[]{3, 3, 3, 3, 3, 3});
//                        table.addCell("Name").setBold();
//                        table.addCell("College").setBold();
//                        table.addCell("Email").setBold();
//                        table.addCell("Phone").setBold();
//                        table.addCell("Payment Method").setBold();
//                        table.addCell("Status").setBold();
//
//                        table.addCell(team.getLeaderName());
//                        table.addCell(team.getLeaderCollege());
//                        table.addCell(team.getLeaderEmail());
//                        table.addCell(team.getLeaderNumber());
//                        table.addCell(team.getPaymentMethod());
//                        table.addCell(team.getStatus());
//                        
//                        for (TeamMember member : team.getMembers()) {
//                            table.addCell(member.getName());
//                            table.addCell(member.getCollege());
//                            table.addCell(member.getEmail());
//                            table.addCell(member.getNumber());
//                            table.addCell("N/A");
//                            table.addCell("N/A");
//                        }
//                        document.add(table);
//                    }
//                }
//            }
//
//            document.close();
//            byte[] pdfBytes = byteArrayOutputStream.toByteArray();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF);
//            headers.setContentDisposition(ContentDisposition.attachment()
//                    .filename("event_participants.pdf")
//                    .build());
//
//            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    

}
