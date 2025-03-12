package com.example.demo.Model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName; // Unique Team Name

    // Leader Information (Stored inside Team)
    private String leaderName;
    private String leaderCollege;
    private String leaderNumber;
    private String leaderEmail;

    // Payment Information
    private String paymentMethod;  // Cash, UPI, Bank Transfer
    @Column(nullable = true)
    private String utrNumber;  // Nullable for cash payments
   
    @Column(nullable = true)
    private String receiptNumber; // Nullable for online payments

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] paymentImage;

    private String status = "pending"; // Default status

    // Many-to-One: Reference to the Event entity
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Events event;

    // One-to-Many with Team Members
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TeamMember> members = new ArrayList<>();

    @Column(nullable = true, updatable = false)
    @CreationTimestamp // Automatically sets the current timestamp
    private Instant registrationDate;
    
    
    @Transient
    private String formattedRegistrationDate;
    
    @PrePersist
    public void prePersist() {
        this.registrationDate = Instant.now();  // Store timestamps in UTC
    }

    public String getFormattedRegistrationDate() {
        if (registrationDate != null) {
            ZonedDateTime istTime = registrationDate
                    .atZone(ZoneId.of("UTC"))   // Treat stored value as UTC
                    .withZoneSameInstant(ZoneId.of("Asia/Kolkata")); // Convert to IST

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return istTime.format(formatter);
        }
        return "No Registration Date";
    }


    
   

    public void setFormattedRegistrationDate(String formattedRegistrationDate) {
        this.formattedRegistrationDate = formattedRegistrationDate;
    }
    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public String getLeaderCollege() {
		return leaderCollege;
	}

	public void setLeaderCollege(String leaderCollege) {
		this.leaderCollege = leaderCollege;
	}

	public String getLeaderNumber() {
		return leaderNumber;
	}

	public void setLeaderNumber(String leaderNumber) {
		this.leaderNumber = leaderNumber;
	}

	public String getLeaderEmail() {
		return leaderEmail;
	}

	public void setLeaderEmail(String leaderEmail) {
		this.leaderEmail = leaderEmail;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public byte[] getPaymentImage() {
		return paymentImage;
	}

	public void setPaymentImage(byte[] paymentImage) {
		this.paymentImage = paymentImage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Events getEvent() {
		return event;
	}

	public void setEvent(Events event) {
		this.event = event;
	}

	public List<TeamMember> getMembers() {
		return members;
	}

	public void setMembers(List<TeamMember> members) {
		this.members = members;
	}

    // Constructors, Getters, and Setters
    
    
}
