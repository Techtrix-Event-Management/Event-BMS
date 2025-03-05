package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

	List<Team> findByEventId(Long eventId);
	
	@Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.event.id = :eventId")
    List<Team> findByEventIdWithMembers(@Param("eventId") Long eventId);
	
	
	List<Team> findByTeamNameContainingIgnoreCaseOrLeaderNameContainingIgnoreCase(String teamName, String leaderName);
	List<Team> findByStatus(String status);
}
