package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.Model.RegisteredStudent;
import com.example.demo.Model.Team;
import com.example.demo.Model.TeamMember;
import com.example.demo.repo.RegisteredStudentRepository;
import com.example.demo.repo.TeamRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RegisteredStudentRepository studentRepository;
    @Autowired
    private TeamRepository teamRepository;

    
    public void sendEmailToAll(String subject, String body) {
        List<RegisteredStudent> students = studentRepository.findByStatus("verified");
        List<Team> teams = teamRepository.findByStatus("verified"); // Fetch verified teams

        if (students.isEmpty() && teams.isEmpty()) {
            System.out.println("No verified students or teams found.");
            return;
        }

        // Sending emails to individual students
        for (RegisteredStudent student : students) {
            String personalizedBody = "Dear " + student.getName() + ",\n" + body;
            sendEmail(student.getEmail(), subject, personalizedBody);
        }

        // Sending emails to team leaders
        for (Team team : teams) {
            String teamLeaderBody = "Dear " + team.getLeaderName() + ",\n" + body;
            sendEmail(team.getLeaderEmail(), subject, teamLeaderBody);

            // Sending emails to team members
            for (TeamMember member : team.getMembers()) {
                String memberBody = "Dear " + member.getName() + ",\n" + body;
                sendEmail(member.getEmail(), subject, memberBody);
            }
        }
    }

    @Transactional
    public void sendEmailToStudent(Long id, String subject, String body) {
        Optional<RegisteredStudent> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            RegisteredStudent student = optionalStudent.get();

            // Update status to "verified" if not already
            if (!"verified".equals(student.getStatus())) {
                student.setStatus("verified");
                studentRepository.save(student); // Save the updated status
                System.out.println("✅ Student status updated to verified.");
            }

            // Remove the "Dear <name>" part, it will be added in frontend
            String messageBody = body;
            sendEmail(student.getEmail(), subject, messageBody);
            System.out.println("✅ Email sent successfully to: " + student.getEmail());
        } else {
            System.out.println("❌ Student not found.");
        }
    }


    @Transactional
    public void sendEmailToTeam(Long teamId, String subject, String body) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);

        if (optionalTeam.isPresent()) {
            Team team = optionalTeam.get();

            // Update team status to "verified" if not already
            if (!"verified".equals(team.getStatus())) {
                team.setStatus("verified");
                teamRepository.save(team); // Save updated status
                System.out.println("✅ Team status updated to verified.");
            }

            // Email content without the "Dear <name>"
            String message = body;

            // Send email to the Team Leader
            sendEmail(team.getLeaderEmail(), subject, message);
            System.out.println("✅ Email sent to team leader: " + team.getLeaderEmail());

            // Send email to all team members
            for (TeamMember member : team.getMembers()) {
                String memberMessage = body;
                sendEmail(member.getEmail(), subject, memberMessage);
                System.out.println("✅ Email sent to team member: " + member.getEmail());
            }
        } else {
            System.out.println("❌ Team not found.");
        }
    }


   
    private void sendEmail(String to, String subject, String body) {
    	 try {
    	        SimpleMailMessage message = new SimpleMailMessage();
    	        message.setFrom("bitcforum2022@gmail.com");
    	        message.setTo(to);
    	        message.setSubject(subject);
    	        message.setText(body);

    	        mailSender.send(message);
    	        System.out.println("✅ Email sent to: " + to);
    	    } catch (Exception e) {
    	        System.out.println("❌ Failed to send email: " + e.getMessage());
    	    }
    }
}