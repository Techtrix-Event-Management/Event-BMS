package com.example.demo.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Events;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
}
