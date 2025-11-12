package com.example.trip.service;

import com.example.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip,Long> {
    List<Trip> getTripsByUser_Id(Long id);
    List<Trip> getTripsByCategories_Id(Long id);

}