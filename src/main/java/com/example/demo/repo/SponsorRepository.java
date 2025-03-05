package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Sponsors;

public interface SponsorRepository extends JpaRepository<Sponsors, Long> {
}