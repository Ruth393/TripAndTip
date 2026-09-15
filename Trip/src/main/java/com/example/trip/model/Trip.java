package com.example.trip.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JsonIgnore
    @NotNull(message = "נדרש משתמש מקושר")
    private Users user;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "trip")
    @JsonIgnore
    private List<Comment> comments;

    public Trip() {}
    // להוסיף בתוך המחלקה Trip, ליד שאר השדות:

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private Difficulty difficulty;

    private Boolean kidFriendly;

    private Double costAmount; // עלות מספרית לצורך סינון טווח (בנוסף ל-cost הטקסטואלי הקיים)

    @Valid
    @NotNull(message = "נדרש מיקום לטיול")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "label", column = @Column(name = "location_label")),
            @AttributeOverride(name = "latitude", column = @Column(name = "location_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_longitude")),
            @AttributeOverride(name = "category", column = @Column(name = "location_category")),
            @AttributeOverride(name = "description", column = @Column(name = "location_description")),
            @AttributeOverride(name = "type", column = @Column(name = "location_type"))
    })
    private TripLocation location;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripImage> images = new ArrayList<>();

    public List<TripImage> getImages() { return images; }
    public void setImages(List<TripImage> images) { this.images = images; }

    public TripLocation getLocation() { return location; }
    public void setLocation(TripLocation location) { this.location = location; }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getKidFriendly() {
        return kidFriendly;
    }

    public void setKidFriendly(Boolean kidFriendly) {
        this.kidFriendly = kidFriendly;
    }

    public Double getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(Double costAmount) {
        this.costAmount = costAmount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
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
}
