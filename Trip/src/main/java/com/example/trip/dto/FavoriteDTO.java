package com.example.trip.dto;

import java.time.LocalDateTime;

public class FavoriteDTO {

    private Long id;
    private TripListDTO trip;
    private LocalDateTime dateAdded;

    public FavoriteDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TripListDTO getTrip() {
        return trip;
    }

    public void setTrip(TripListDTO trip) {
        this.trip = trip;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
}