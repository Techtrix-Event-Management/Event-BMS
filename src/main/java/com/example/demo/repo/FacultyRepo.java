package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Faculty;

@Repository
public interface FacultyRepo extends JpaRepository<Faculty, Long> {
	Optional<Faculty> findByEmail(String email);
	boolean existsByEmail(String email);
	

}
