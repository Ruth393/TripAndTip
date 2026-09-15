package com.example.trip.controller;

import com.example.trip.dto.DirectionsRequest;
import com.example.trip.dto.DirectionsResponse;
import com.example.trip.service.DirectionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/directions")
public class DirectionsController {

    @Autowired
    private DirectionsService directionsService;

    @PostMapping
    public DirectionsResponse getDirections(@Valid @RequestBody DirectionsRequest request) {
        return directionsService.getDirections(request);
    }
}