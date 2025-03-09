package com.example.demo.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.RegisteredStudent;

@Repository
public interface RegisteredStudentRepository extends JpaRepository<RegisteredStudent, Long> {
    List<RegisteredStudent> findByNameContainingIgnoreCase(String name);
    List<RegisteredStudent> findByEventId(Long eventId);
    List<RegisteredStudent> findByEventIdAndStatus(Long eventId, String status);
    List<RegisteredStudent> findByStatus(String status);
    Page<RegisteredStudent> findByEventId(Long eventId, Pageable pageable);
    Page<RegisteredStudent> findByEventIdAndStatus(Long eventId, String status, Pageable pageable);
}
