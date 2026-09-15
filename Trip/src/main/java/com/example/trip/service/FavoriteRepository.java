package com.example.trip.service;

import com.example.trip.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser_IdOrderByDateAddedDesc(Long userId);

    boolean existsByUser_IdAndTrip_Id(Long userId, Long tripId);

    void deleteByUser_IdAndTrip_Id(Long userId, Long tripId);

    long countByTrip_Id(Long tripId);

    void deleteByTrip_Id(Long tripId);
    long countByTrip_User_Id(Long userId);
}