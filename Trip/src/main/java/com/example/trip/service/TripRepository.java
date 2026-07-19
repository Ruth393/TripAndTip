package com.example.trip.service;

import com.example.trip.model.Trip;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {
    List<Trip> getTripsByUser_Id(Long id);

    List<Trip> getTripsByCategory_Id(Long Id);

    @Query("SELECT t FROM Trip t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Trip> searchTripsByKeyword(@Param("keyword") String keyword);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.comments WHERE t.id = :id")
    Optional<Trip> findByIdWithComments(@Param("id") Long id);
}