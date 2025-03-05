package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Events;
import com.example.demo.repo.EventRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Optional<Events> getEventById(Long id) {
        return eventRepository.findById(id);
    }
    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public Events saveEvent(Events event) {
        return eventRepository.save(event);
    }

    

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}