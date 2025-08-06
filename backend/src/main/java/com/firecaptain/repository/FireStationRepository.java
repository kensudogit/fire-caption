package com.firecaptain.repository;

import com.firecaptain.entity.FireStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FireStationRepository extends JpaRepository<FireStation, Long> {

    Optional<FireStation> findByStationCode(String stationCode);

    List<FireStation> findByIsActiveTrue();

    List<FireStation> findByStationType(FireStation.StationType stationType);

    @Query("SELECT fs FROM FireStation fs WHERE " +
            "6371 * acos(cos(radians(:latitude)) * cos(radians(fs.latitude)) * " +
            "cos(radians(fs.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(fs.latitude))) <= :radius")
    List<FireStation> findStationsWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radiusKm);

    @Query("SELECT fs FROM FireStation fs WHERE fs.isActive = true AND " +
            "fs.capacity > (SELECT COUNT(f) FROM Firefighter f WHERE f.fireStation = fs AND f.status = 'ACTIVE')")
    List<FireStation> findAvailableStations();

    boolean existsByStationCode(String stationCode);
}
