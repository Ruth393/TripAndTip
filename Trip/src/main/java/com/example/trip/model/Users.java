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
    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Trip> trips;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Comment> comments;


    @ManyToMany
    private Set<Role> roles=new HashSet<>();


    public Users() {
    }

    public Users(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}