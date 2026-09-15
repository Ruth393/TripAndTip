package com.example.trip.dto;

import java.util.List;

public class DirectionsResponse {

    private double distanceMeters;
    private double durationSeconds;
    private List<double[]> geometry; // כל איבר: [lat, lng]

    public DirectionsResponse() {}

    public DirectionsResponse(double distanceMeters, double durationSeconds, List<double[]> geometry) {
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.geometry = geometry;
    }

    public double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }
    public double getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(double durationSeconds) { this.durationSeconds = durationSeconds; }
    public List<double[]> getGeometry() { return geometry; }
    public void setGeometry(List<double[]> geometry) { this.geometry = geometry; }
}