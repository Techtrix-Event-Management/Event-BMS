package com.example.demo.Model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class RegisteredStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Long id;

    private String name;
    private String college;
    private String number;
    private String paymentMethod;  // New Field
    
    @Column(nullable = true) // Nullable for cash payments
    private String utrNumber;      // New Field
    
    @Column(nullable = true) // Nullable for online payments
    private String receiptNumber;

    private String email;
    
    @Transient
    private String paymentImageBase64;  // Transient field to store Base64 image

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] paymentImage;

    // Status variable with default value "pending"
    private String status = "pending";

    // Many-to-One: Reference to the Event entity
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false) // Foreign key column
    private Events event;

    
    @Column(nullable =true, updatable = false)
    @CreationTimestamp // Automatically sets the current timestamp
    private Instant registrationDate;
    
    
    @Transient
    private String formattedRegistrationDate;
    
    @PrePersist
    public void prePersist() {
        this.registrationDate = Instant.now();  // Always store in UTC
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


    
    // Constructors
    public RegisteredStudent() {}

    public RegisteredStudent(String name, String college, String number, String email, 
                             String paymentMethod, String utrNumber, String receiptNumber,
                             byte[] paymentImage, Events event, String status) {
        this.name = name;
        this.college = college;
        this.number = number;
        this.email = email;
        this.paymentMethod = paymentMethod;
        this.utrNumber = utrNumber;
        this.receiptNumber = receiptNumber;
        this.paymentImage = paymentImage;
        this.event = event;
        this.status = status;
    }
    
    

    public void setFormattedRegistrationDate(String formattedRegistrationDate) {
        this.formattedRegistrationDate = formattedRegistrationDate;
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   
    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
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

	public String getPaymentImageBase64() {
		return paymentImageBase64;
	}

	public void setPaymentImageBase64(String paymentImageBase64) {
		this.paymentImageBase64 = paymentImageBase64;
	}
    
    
}
