package com.example.trip.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TripListDTO {
    private Long id;
    @NotBlank(message = "נדרש שם הטיול")
    private String name;
    @Size(max = 1000, message = "תיאור הטיול ארוך מדי (עד 1000 תווים)")
    private String description;
    @Size(max = 255, message = "נתיב התמונה ארוך מדי")
    private String imagePath;
    private String image;
    @NotNull(message = "נדרש משתמש מקושר")
    private UserToSeeDTO user;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserToSeeDTO getUser() {
        return user;
    }

    public void setUser(UserToSeeDTO user) {
        this.user = user;
    }
}
