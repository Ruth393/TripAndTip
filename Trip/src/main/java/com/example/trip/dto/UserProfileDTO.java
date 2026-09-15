package com.example.trip.dto;

public class UserProfileDTO {
    private Long id;
    private String userName;
    private String image;
    private String imagePath;
    // ─── חדש ───
    private String googleImageUrl;
    private String bio;
    private long tripsCount;
    private long totalFavoritesReceived;
    private long totalRatingsReceived;
    private Double averageRating;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    // ─── חדש ───
    public String getGoogleImageUrl() { return googleImageUrl; }
    public void setGoogleImageUrl(String googleImageUrl) { this.googleImageUrl = googleImageUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public long getTripsCount() { return tripsCount; }
    public void setTripsCount(long tripsCount) { this.tripsCount = tripsCount; }
    public long getTotalFavoritesReceived() { return totalFavoritesReceived; }
    public void setTotalFavoritesReceived(long v) { this.totalFavoritesReceived = v; }
    public long getTotalRatingsReceived() { return totalRatingsReceived; }
    public void setTotalRatingsReceived(long v) { this.totalRatingsReceived = v; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
}