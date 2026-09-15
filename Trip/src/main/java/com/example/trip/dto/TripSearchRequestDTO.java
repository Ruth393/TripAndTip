package com.example.trip.dto;

public class TripSearchRequestDTO {
    private Long categoryId;
    private String difficulty; // EASY / MEDIUM / HARD
    private Boolean kidFriendly;
    private Double minCost;
    private Double maxCost;
    private String keyword;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Boolean getKidFriendly() { return kidFriendly; }
    public void setKidFriendly(Boolean kidFriendly) { this.kidFriendly = kidFriendly; }

    public Double getMinCost() { return minCost; }
    public void setMinCost(Double minCost) { this.minCost = minCost; }

    public Double getMaxCost() { return maxCost; }
    public void setMaxCost(Double maxCost) { this.maxCost = maxCost; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}