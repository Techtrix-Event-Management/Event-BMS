package com.example.demo.controller;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-to-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmailToAll(@RequestParam String subject, @RequestParam String body) {
        emailService.sendEmailToAll(subject, body);
        return ResponseEntity.ok("✅ Emails sent to all verified students.");
    }

    @PostMapping("/send-to-student/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmailToStudent(@PathVariable Long id, 
                                                     @RequestParam String subject, 
                                                     @RequestParam String body) {
        emailService.sendEmailToStudent(id, subject, body);
        return ResponseEntity.ok("✅ Email sent to student with ID: " + id);
    }
    
    @PostMapping("/send-to-team/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmailToTeam(@PathVariable Long teamId, 
                                                  @RequestParam String subject, 
                                                  @RequestParam String body) {
        emailService.sendEmailToTeam(teamId, subject, body);
        return ResponseEntity.ok("✅ Email sent to team with ID: " + teamId);
    }
    
    
 
    
}