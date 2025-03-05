package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.SponsorImage;

@Repository
public interface SponsorImageRepository extends JpaRepository<SponsorImage, Long> {
}
