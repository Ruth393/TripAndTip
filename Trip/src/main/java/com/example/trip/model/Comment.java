package com.example.trip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;
    private String comment;
    private LocalDate date;

    @ManyToOne
    @JsonIgnore
    private Users user;

    @ManyToOne
    @JsonIgnore
    private Trip trips;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Trip getTrips() {
        return trips;
    }

    public void setTrips(Trip trips) {
        this.trips = trips;
    }

    public Comment() {
    }


}
