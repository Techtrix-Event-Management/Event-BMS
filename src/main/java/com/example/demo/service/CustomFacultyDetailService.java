package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Faculty;
import com.example.demo.repo.FacultyRepo;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class CustomFacultyDetailService implements UserDetailsService {

    private final FacultyRepo facultyRepo;

    public CustomFacultyDetailService(FacultyRepo facultyRepo) {
        this.facultyRepo = facultyRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Faculty faculty = facultyRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Faculty not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                faculty.getEmail(), faculty.getPassword(), Collections.emptyList());
    }
}
