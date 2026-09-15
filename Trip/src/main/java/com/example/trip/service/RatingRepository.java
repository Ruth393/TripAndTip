package com.example.trip.service;

import com.example.trip.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> getRatingsByTrip_IdOrderByDateDesc(Long tripId);

    Optional<Rating> findByUser_IdAndTrip_Id(Long userId, Long tripId);

    void deleteByUser_IdAndTrip_Id(Long userId, Long tripId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.trip.id = :tripId")
    Double getAverageRatingForTrip(@Param("tripId") Long tripId);

    long countByTrip_Id(Long tripId);

    void deleteByTrip_Id(Long tripId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.trip.user.id = :userId")
    Double getAverageRatingForUser(@Param("userId") Long userId);

    long countByTrip_User_Id(Long userId);
}