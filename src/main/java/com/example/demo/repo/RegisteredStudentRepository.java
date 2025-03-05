package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.RegisteredStudent;

@Repository
public interface RegisteredStudentRepository extends JpaRepository<RegisteredStudent, Long> {
    List<RegisteredStudent> findByNameContainingIgnoreCase(String name);
    List<RegisteredStudent> findByStatus(String status);
    List<RegisteredStudent> findByEventId(Long eventId);

}
