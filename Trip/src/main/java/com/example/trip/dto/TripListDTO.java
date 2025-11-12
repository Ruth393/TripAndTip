package com.example.trip.dto;



public class TripListDTO {
    private Long id;
    private String name;
    private String imagePath;
    private String image;
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
