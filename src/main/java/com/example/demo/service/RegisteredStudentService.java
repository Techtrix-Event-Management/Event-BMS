package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Events;
import com.example.demo.Model.RegisteredStudent;
import com.example.demo.Model.Team;
import com.example.demo.Model.TeamMember;
import com.example.demo.repo.EventRepository;
import com.example.demo.repo.RegisteredStudentRepository;
import com.example.demo.repo.TeamMemberRepository;
import com.example.demo.repo.TeamRepository;

import org.springframework.data.domain.Sort;


@Service
public class RegisteredStudentService {

    @Autowired
    private RegisteredStudentRepository registeredStudentRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;

    public void registerStudent(String name, String college, String number, String email,
         String paymentMethod, String utrNumber, String receiptNumber,
         MultipartFile file, Events event) throws IOException {

			RegisteredStudent student = new RegisteredStudent();
			student.setName(name);
			student.setCollege(college);
			student.setNumber(number);
			student.setEmail(email);
			student.setPaymentMethod(paymentMethod);
			student.setEvent(event);
			student.setStatus("pending"); // Registration pending until faculty approval
			
			if ("cash".equalsIgnoreCase(paymentMethod)) {
			student.setReceiptNumber(receiptNumber); // Save receipt number
			student.setPaymentImage(file.getBytes()); // Save receipt image
			student.setUtrNumber(null); // No UTR for cash payments
			} else {
			student.setUtrNumber(utrNumber); // Save UTR for online payments
			student.setPaymentImage(file.getBytes()); // Save payment screenshot
			student.setReceiptNumber(null); // No receipt for online payments
		}

			registeredStudentRepository.save(student); // Save student registration
    }
    
    
    public Team registerTeam(
            String teamName, String leaderName, String leaderCollege, String leaderNumber, String leaderEmail,
            String paymentMethod, String utrNumber, String receiptNumber, Long eventId, byte[] paymentImage,
            List<TeamMember> teamMembers
        ) {

            Events event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

            Team team = new Team();
            team.setTeamName(teamName);
            team.setLeaderName(leaderName);
            team.setLeaderCollege(leaderCollege);
            team.setLeaderNumber(leaderNumber);
            team.setLeaderEmail(leaderEmail);
            team.setPaymentMethod(paymentMethod);
            team.setUtrNumber(utrNumber);
            team.setReceiptNumber(receiptNumber);
            team.setEvent(event);
            team.setPaymentImage(paymentImage);
            team.setStatus("pending");

            // Save team first to get an ID
            Team savedTeam = teamRepository.save(team);

            // Associate members with the team
            for (TeamMember member : teamMembers) {
                member.setTeam(savedTeam);
                teamMemberRepository.save(member);
            }

            return savedTeam;
        }
    
    
}
