package com.example.trip.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RatingDTO {

    private Long id;

    @NotNull(message = "נדרש ציון דירוג")
    @Min(value = 1, message = "הדירוג חייב להיות בין 1 ל-5")
    @Max(value = 5, message = "הדירוג חייב להיות בין 1 ל-5")
    private Integer stars;

    @Size(max = 500, message = "אורך הביקורת מוגבל ל-500 תווים")
    private String review;

    private LocalDate date;

    private UserToSeeDTO user;

    public RatingDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public UserToSeeDTO getUser() {
        return user;
    }

    public void setUser(UserToSeeDTO user) {
        this.user = user;
    }
}