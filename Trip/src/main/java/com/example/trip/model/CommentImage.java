package com.example.trip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class CommentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    @JsonIgnore
    private Comment comment;

    public CommentImage() {}

    public CommentImage(String imagePath, Comment comment) {
        this.imagePath = imagePath;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }
}