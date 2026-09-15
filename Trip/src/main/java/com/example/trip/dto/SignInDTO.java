package com.example.trip.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class SignInDTO {

    private Long id;

    @NotBlank(message = "נדרש מייל המשתמש")
    private String email;
    private List<String> roles;
    private String image;
    private String imagePath;

    // ─── חדש: כתובת תמונת הפרופיל מגוגל (URL מלא, ריק אצל משתמשים רגילים) ───
    private String googleImageUrl;

    public SignInDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    // ─── חדש ───
    public String getGoogleImageUrl() {
        return googleImageUrl;
    }

    public void setGoogleImageUrl(String googleImageUrl) {
        this.googleImageUrl = googleImageUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}