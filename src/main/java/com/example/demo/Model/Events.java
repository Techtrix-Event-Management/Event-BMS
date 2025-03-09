package com.example.demo.Model;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String eventName;
    private String description;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] image;
    
    @Transient
    private String imageBase64;
    private boolean registrationOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    @JsonBackReference
    private Faculty faculty;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] rulebook;
    
    @Transient
    private String rulebookBase64;
    
    private boolean isTeamParticipation;
    
    private Integer maxAllowedParticipants; 
    private Integer maxTeamSize;
    
 // One-to-Many with RegisteredStudent
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegisteredStudent> students = new ArrayList<>();

    // One-to-Many with Team
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();
    
    
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

 

    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

	public Faculty getFaculty() {
		return faculty;
	}

	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public byte[] getRulebook() {
		return rulebook;
	}

	public void setRulebook(byte[] rulebook) {
		this.rulebook = rulebook;
	}

	public boolean isTeamParticipation() {
		return isTeamParticipation;
	}

	public void setTeamParticipation(boolean isTeamParticipation) {
		this.isTeamParticipation = isTeamParticipation;
	}

	public Integer getMaxTeamSize() {
		return maxTeamSize;
	}

	public void setMaxTeamSize(Integer maxTeamSize) {
		this.maxTeamSize = maxTeamSize;
	}

	public List<RegisteredStudent> getStudents() {
		return students;
	}

	public void setStudents(List<RegisteredStudent> students) {
		this.students = students;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}

	public String getRulebookBase64() {
		return rulebookBase64;
	}

	public void setRulebookBase64(String rulebookBase64) {
		this.rulebookBase64 = rulebookBase64;
	}

	public Integer getMaxAllowedParticipants() {
		return maxAllowedParticipants;
	}

	public void setMaxAllowedParticipants(Integer maxAllowedParticipants) {
		this.maxAllowedParticipants = maxAllowedParticipants;
	}
    
	
}
