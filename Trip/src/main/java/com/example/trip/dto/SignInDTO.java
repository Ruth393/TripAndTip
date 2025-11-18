package com.example.trip.dto;


import com.example.trip.model.Comment;
import com.example.trip.model.Role;
import com.example.trip.model.Trip;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SignInDTO {

    private Long id;
    private String userName;
    private String password;

    private Set<Role> roles=new HashSet<>();


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}