package com.example.trip.dto;

import com.example.trip.model.LocationType;

public class TripLocationDTO {

    private String label;
    private Double latitude;
    private Double longitude;
    private String category;
    private String description;
    private LocationType type;

    public TripLocationDTO() {}

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