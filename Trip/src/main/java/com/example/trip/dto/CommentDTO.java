package com.example.trip.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CommentDTO {
    private Long id;
    @NotBlank(message = "תוכן התגובה לא יכול להיות ריק")
    @Size(min = 1, max = 500, message = "אורך התגובה מוגבל ל-500 תווים")
    private String comment;
    @NotNull(message = "תאריך התגובה נדרש")
    @PastOrPresent(message = "תאריך התגובה צריך להיות תאריך נוכחי")
    private LocalDate date;

    private UserToSeeDTO user;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserToSeeDTO getUser() {
        return user;
    }

    public void setUser(UserToSeeDTO user) {
        this.user = user;
    }

}

