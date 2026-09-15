package com.example.trip.dto;

import jakarta.validation.constraints.NotNull;

public class DirectionsRequest {

    @NotNull(message = "נדרשת נקודת מוצא")
    private Double startLat;

    @NotNull(message = "נדרשת נקודת מוצא")
    private Double startLng;

    @NotNull(message = "נדרשת נקודת יעד")
    private Double endLat;

    @NotNull(message = "נדרשת נקודת יעד")
    private Double endLng;

    /** foot-walking | foot-hiking | driving-car | cycling-regular */
    @NotNull(message = "נדרש סוג מסלול")
    private String profile;

    public Double getStartLat() { return startLat; }
    public void setStartLat(Double startLat) { this.startLat = startLat; }
    public Double getStartLng() { return startLng; }
    public void setStartLng(Double startLng) { this.startLng = startLng; }
    public Double getEndLat() { return endLat; }
    public void setEndLat(Double endLat) { this.endLat = endLat; }
    public Double getEndLng() { return endLng; }
    public void setEndLng(Double endLng) { this.endLng = endLng; }
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}