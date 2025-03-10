package com.example.demo.controller;

import com.example.demo.Model.Events;
import com.example.demo.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEvent(
        @RequestParam("eventName") String eventName,
        @RequestParam("description") String description,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "rulebook", required = false) MultipartFile rulebook,
        @RequestParam("registrationOpen") boolean registrationOpen,
        @RequestParam("isTeamParticipation") boolean isTeamParticipation,
        @RequestParam(value = "maxTeamSize", required = false) Integer maxTeamSize,
        @RequestParam(value = "maxAllowedParticipants", required = false) Integer maxAllowedParticipants
    ) {
        try {
            Events event = new Events();
            event.setEventName(eventName);
            event.setDescription(description);
            event.setRegistrationOpen(registrationOpen);
            event.setTeamParticipation(isTeamParticipation);

            if (isTeamParticipation) {
                if (maxTeamSize == null || maxTeamSize < 1) {
                    return ResponseEntity.badRequest().body("Max team size is required and must be at least 1 if team participation is enabled.");
                }
                if (maxAllowedParticipants == null || maxAllowedParticipants < 1 || maxAllowedParticipants > maxTeamSize) {
                    return ResponseEntity.badRequest().body("Max allowed participants must be between 1 and the max team size.");
                }
                event.setMaxTeamSize(maxTeamSize);
                event.setMaxAllowedParticipants(maxAllowedParticipants);
            } else {
                event.setMaxTeamSize(null);
                event.setMaxAllowedParticipants(null);
            }

            if (image != null) {
                event.setImage(image.getBytes());
            }
            if (rulebook != null) {
                event.setRulebook(rulebook.getBytes());
            }

            Events savedEvent = eventService.saveEvent(event);
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving event: " + e.getMessage());
        }
    }


    @PutMapping("/edit/{id}") 
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editEvent(
        @PathVariable Long id,
        @RequestParam("eventName") String eventName,
        @RequestParam("description") String description,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "rulebook", required = false) MultipartFile rulebook,
        @RequestParam("registrationOpen") boolean registrationOpen,
        @RequestParam("isTeamParticipation") boolean isTeamParticipation,
        @RequestParam(value = "maxTeamSize", required = false) Integer maxTeamSize,
        @RequestParam(value = "maxAllowedParticipants", required = false) Integer maxAllowedParticipants
    ) {
        try {
            Optional<Events> existingEventOpt = eventService.getEventById(id);
            if (!existingEventOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
            }

            Events existingEvent = existingEventOpt.get();
            existingEvent.setEventName(eventName);
            existingEvent.setDescription(description);
            existingEvent.setRegistrationOpen(registrationOpen);
            existingEvent.setTeamParticipation(isTeamParticipation);

            if (isTeamParticipation) {
                if (maxTeamSize == null || maxTeamSize < 1) {
                    return ResponseEntity.badRequest().body("Max team size must be at least 1.");
                }
                if (maxAllowedParticipants == null || maxAllowedParticipants < 1 || maxAllowedParticipants > maxTeamSize) {
                    return ResponseEntity.badRequest().body("Max allowed participants must be between 1 and max team size.");
                }
                existingEvent.setMaxTeamSize(maxTeamSize);
                existingEvent.setMaxAllowedParticipants(maxAllowedParticipants);
            } else {
                existingEvent.setMaxTeamSize(null);
                existingEvent.setMaxAllowedParticipants(null);
            }

            Events updatedEvent = eventService.saveEvent(existingEvent);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating event: " + e.getMessage());
        }
    }

    
   
    @GetMapping
    public List<Map<String, Object>> getAllEvents() {
        return eventService.getAllEvents().stream().map(event -> {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("id", event.getId());
            eventMap.put("eventName", event.getEventName());
            eventMap.put("description", event.getDescription());
            eventMap.put("registrationOpen", event.isRegistrationOpen());
            eventMap.put("isTeamParticipation", event.isTeamParticipation());
            eventMap.put("maxTeamSize", event.getMaxTeamSize());
            eventMap.put("maxAllowedParticipants", event.getMaxAllowedParticipants());
            if (event.getImage() != null) {
                eventMap.put("image", "data:image/png;base64," + Base64.getEncoder().encodeToString(event.getImage()));
            } else {
                eventMap.put("image", null);
            }

            if (event.getRulebook() != null) {
                eventMap.put("rulebook", "data:application/pdf;base64," + Base64.getEncoder().encodeToString(event.getRulebook()));
            } else {
                eventMap.put("rulebook", null);
            }

            return eventMap;
        }).collect(Collectors.toList());
    }
    	
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Optional<Events> eventOpt = eventService.getEventById(id);

        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        Events event = eventOpt.get();

        // Convert image and rulebook bytes into Base64 for frontend
        Map<String, Object> response = new HashMap<>();
        response.put("id", event.getId());
        response.put("eventName", event.getEventName());
        response.put("description", event.getDescription());
        response.put("maxTeamSize", event.getMaxTeamSize());
        response.put("registrationOpen", event.isRegistrationOpen());
        response.put("isTeamParticipation", event.isTeamParticipation());
        response.put("maxAllowedParticipants", event.getMaxAllowedParticipants());
        
        // Only include image if it exists
        if (event.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(event.getImage());
            response.put("image", base64Image);
        }

        // Only include rulebook if it exists
        if (event.getRulebook() != null) {
            String base64Rulebook = Base64.getEncoder().encodeToString(event.getRulebook());
            response.put("rulebook", base64Rulebook);
        }

        // Faculty details
        if (event.getFaculty() != null) {
            Map<String, Object> facultyDetails = new HashMap<>();
            facultyDetails.put("id", event.getFaculty().getId());
            facultyDetails.put("name", event.getFaculty().getName());
            response.put("faculty", facultyDetails);
        }

        // Omit registered students and teams data to improve performance

        return ResponseEntity.ok(response);
    }


   


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
