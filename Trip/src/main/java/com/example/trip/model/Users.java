package com.example.trip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Users {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String email;
    private String password;
    private String image;
    private String imagePath;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Trip> trips;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Comment> comments;


    @ManyToMany
    @JsonIgnore
    private Set<Role> roles=new HashSet<>();


    public Users() {
    }

    public Users(String userName, List<Trip> trips, Set<Role> roles, String password, String imagePath, String image, Long id, String email, List<Comment> comments) {
        this.userName = userName;
        this.trips = trips;
        this.roles = roles;
        this.password = password;
        this.imagePath = imagePath;
        this.image = image;
        this.id = id;
        this.email = email;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<Role> getRoles() {
    return roles;
}

    public void setRoles(Set<Role> roles) {
    this.roles = roles;
}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}