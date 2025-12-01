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
    private String image;
    private String imagePath;


    public SignInDTO() {
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