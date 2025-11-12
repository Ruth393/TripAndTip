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
        Category beach = new Category();
        beach.setCategory("Beach");

        Category mountain = new Category();
        mountain.setCategory("Mountain");

        Category city = new Category();
        city.setCategory("City");

        categoryRepository.saveAll(List.of(beach, mountain, city));

        Trip trip1 = new Trip();
        trip1.setName("Holiday in Spain");
        trip1.setCategories(List.of(beach, city));

        Trip trip2 = new Trip();
        trip2.setName("Adventure in Alps");
        trip2.setCategories(List.of(mountain));

        Trip trip3 = new Trip();
        trip3.setName("City Tour in Paris");

        trip3.setCategories(List.of(city));

        tripRepository.saveAll(List.of(trip1, trip2, trip3));
    }
}
