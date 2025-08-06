package com.firecaptain.repository;

import com.firecaptain.entity.Firefighter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FirefighterRepository extends JpaRepository<Firefighter, Long> {

    Optional<Firefighter> findByEmployeeId(String employeeId);

    Optional<Firefighter> findByEmail(String email);

    List<Firefighter> findByStatus(Firefighter.Status status);

    List<Firefighter> findByRank(Firefighter.Rank rank);

    List<Firefighter> findByFireStationId(Long fireStationId);

    @Query("SELECT f FROM Firefighter f WHERE f.fireStation.id = :stationId AND f.status = 'ACTIVE'")
    List<Firefighter> findActiveFirefightersByStation(@Param("stationId") Long stationId);

    @Query("SELECT f FROM Firefighter f WHERE f.status = 'ACTIVE' AND " +
            "f.fireStation.id IN (SELECT fs.id FROM FireStation fs WHERE fs.isActive = true)")
    List<Firefighter> findAvailableFirefighters();

    @Query("SELECT f FROM Firefighter f WHERE f.status = 'ACTIVE' AND " +
            "f.rank IN ('CAPTAIN', 'LIEUTENANT', 'BATTALION_CHIEF', 'DEPUTY_CHIEF', 'FIRE_CHIEF')")
    List<Firefighter> findOfficers();

    @Query("SELECT COUNT(f) FROM Firefighter f WHERE f.fireStation.id = :stationId AND f.status = 'ACTIVE'")
    long countActiveFirefightersByStation(@Param("stationId") Long stationId);

    Page<Firefighter> findByFireStationIdAndStatus(Long fireStationId, Firefighter.Status status, Pageable pageable);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);
}
