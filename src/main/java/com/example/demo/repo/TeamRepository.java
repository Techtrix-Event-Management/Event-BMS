package com.example.demo.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

	
	
	Page<Team> findByEventId(Long eventId, Pageable pageable);

	
	List<Team> findByTeamNameContainingIgnoreCaseOrLeaderNameContainingIgnoreCase(String teamName, String leaderName);
	List<Team> findByStatus(String status);
	List<Team> findByEventId(Long eventId);
    List<Team> findByEventIdAndStatus(Long eventId, String status);
	Page<Team> findByEventIdAndStatus(Long eventId, String status, Pageable pageable);
}


