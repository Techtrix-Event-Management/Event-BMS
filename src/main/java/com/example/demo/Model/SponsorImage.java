package com.example.demo.Model;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class SponsorImage {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] imageData;  // Store image as a byte array in DB

    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = false)
    @JsonBackReference
    private Sponsors sponsor;

 // Convert image byte array to Base64 string
    public String getImageBase64() {
        return (imageData != null) ? "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData) : null;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public Sponsors getSponsor() {
		return sponsor;
	}

	public void setSponsor(Sponsors sponsor) {
		this.sponsor = sponsor;
	}

    
    
    // Constructors, Getters, Setters
}