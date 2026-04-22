package com.example.trip.service;

import com.example.trip.model.Trip;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {
    List<Trip> getTripsByUser_Id(Long id);

    List<Trip> getTripsByCategory_Id(Long Id);

}