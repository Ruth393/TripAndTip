package com.example.trip.dto;

public class RatingSummaryDTO {

    private Double averageRating;
    private long totalRatings;

    public RatingSummaryDTO() {
    }

    public RatingSummaryDTO(Double averageRating, long totalRatings) {
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }
}