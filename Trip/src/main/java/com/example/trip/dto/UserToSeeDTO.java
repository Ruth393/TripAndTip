package com.example.trip.dto;

import jakarta.validation.constraints.NotBlank;

public class UserToSeeDTO {
    private Long id;
    @NotBlank
    private String userName;
    private String image;
    private String imagePath;

    public UserToSeeDTO() {
    }

    public UserToSeeDTO(String userName, String imagePath, String image, Long id) {
        this.userName = userName;
        this.imagePath = imagePath;
        this.image = image;
        this.id = id;
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
