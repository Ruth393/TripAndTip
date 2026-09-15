package com.example.trip.dto;

import com.example.trip.model.Trip;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.util.List;

public class CommentToAddDTO {
    private Long id;
    private String comment;
    private LocalDate date;
    private UserToSeeDTO user;

    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    private List<ImageDTO> images;

    public List<ImageDTO> getImages() { return images; }
    public void setImages(List<ImageDTO> images) { this.images = images; }

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
