package com.firecaptain.repository;

import com.firecaptain.entity.EmergencyCall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyCallRepository extends JpaRepository<EmergencyCall, Long> {

    Optional<EmergencyCall> findByCallNumber(String callNumber);

    Page<EmergencyCall> findByStatus(EmergencyCall.CallStatus status, Pageable pageable);

    Page<EmergencyCall> findByIncidentType(EmergencyCall.IncidentType incidentType, Pageable pageable);

    Page<EmergencyCall> findByPriorityLevel(EmergencyCall.PriorityLevel priorityLevel, Pageable pageable);

    @Query("SELECT ec FROM EmergencyCall ec WHERE ec.receivedAt BETWEEN :startDate AND :endDate")
    List<EmergencyCall> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ec FROM EmergencyCall ec WHERE ec.status IN ('PENDING', 'DISPATCHED', 'EN_ROUTE', 'ON_SCENE')")
    List<EmergencyCall> findActiveCalls();

    @Query("SELECT ec FROM EmergencyCall ec WHERE " +
            "6371 * acos(cos(radians(:latitude)) * cos(radians(ec.latitude)) * " +
            "cos(radians(ec.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(ec.latitude))) <= :radius")
    List<EmergencyCall> findCallsWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radiusKm);

    @Query("SELECT COUNT(ec) FROM EmergencyCall ec WHERE ec.status = :status")
    long countByStatus(@Param("status") EmergencyCall.CallStatus status);

    @Query("SELECT ec FROM EmergencyCall ec WHERE ec.assignedStation.id = :stationId")
    List<EmergencyCall> findByAssignedStation(@Param("stationId") Long stationId);

    boolean existsByCallNumber(String callNumber);
}
