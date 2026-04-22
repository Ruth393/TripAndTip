package com.example.trip.dto;


import com.example.trip.model.Category;
import com.example.trip.model.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.List;

public class TripDTO {

    private Long id;
    @NotBlank(message = "נדרש שם הטיול")
    private String name;
    @Size(max = 1000, message = "תיאור הטיול ארוך מדי (עד 1000 תווים)")
    private String description;
    @NotBlank(message = "עלות נדרשת")
    @Size(max = 50, message = "פורמט העלות ארוך מדי")
    private String cost;
    @Size(max = 50, message = "שדה התאמה ארוך מדי")
    private String match;
    @Size(max = 255, message = "נתיב התמונה ארוך מדי")
    private String imagePath;
    private String image;
    @NotNull(message = "נדרש משתמש מקושר")
    private UserToSeeDTO user;



    public UserToSeeDTO getUser() {
        return user;
    }

    public void setUser(UserToSeeDTO user) {
        this.user = user;
    }

    private Category category;

    private List<Comment> comments;

    public TripDTO() {
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

