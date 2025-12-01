package com.example.trip.service;

import com.example.trip.model.Trip;
import com.example.trip.model.Category;
import com.example.trip.service.TripRepository;
import com.example.trip.service.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TripCategoryInitializer implements CommandLineRunner {

    private TripRepository tripRepository;
    private CategoryRepository categoryRepository;

    public TripCategoryInitializer(TripRepository tripRepository, CategoryRepository categoryRepository) {
        this.tripRepository = tripRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

    }
}
