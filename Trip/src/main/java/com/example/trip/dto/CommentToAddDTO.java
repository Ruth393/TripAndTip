package com.example.trip.dto;

import com.example.trip.model.Trip;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;

public class CommentToAddDTO {
    private Long id;
    private String comment;
    private LocalDate date;
    private UserToSeeDTO user;

    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserToSeeDTO getUser() {
        return user;
    }

    public void setUser(UserToSeeDTO user) {
        this.user = user;
    }

    public  Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
