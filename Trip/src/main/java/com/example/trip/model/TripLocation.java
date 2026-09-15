package com.example.trip.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class TripLocation {

    @Size(max = 255, message = "התווית ארוכה מדי")
    private String label;

    @NotNull(message = "נדרש קו רוחב")
    private Double latitude;

    @NotNull(message = "נדרש קו אורך")
    private Double longitude;

    @Size(max = 100, message = "הקטגוריה ארוכה מדי")
    private String category;

    @Size(max = 1000, message = "התיאור ארוך מדי")
    private String description;

    @Enumerated(EnumType.STRING)
    private LocationType type;

    public TripLocation() {}

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocationType getType() { return type; }
    public void setType(LocationType type) { this.type = type; }
}