package com.example.trip.dto;


import com.example.trip.model.Category;
import com.example.trip.model.Comment;


import java.util.List;

public class TripDTO {

    private Long id;
    private String name;
    private String description;
    private String cost;
    private String match;
    private String imagePath;
    private String image;

    private UserToSeeDTO user;


    public UserToSeeDTO getUser() {
        return user;
    }

    public void setUser(UserToSeeDTO user) {
        this.user = user;
    }

    private List<Category> categories;

    private List<Comment> comments;

    public TripDTO() {
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

