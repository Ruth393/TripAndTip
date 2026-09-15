package com.example.trip.service;

import com.example.trip.dto.RatingDTO;
import com.example.trip.dto.RatingSummaryDTO;
import com.example.trip.mapper.RatingMapper;
import com.example.trip.model.Rating;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final TripRepository tripRepository;
    private final RatingMapper ratingMapper;

    public RatingService(RatingRepository ratingRepository,
                         TripRepository tripRepository,
                         RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.tripRepository = tripRepository;
        this.ratingMapper = ratingMapper;
    }

    // הוספה או עדכון (upsert) - משתמש יכול לדרג טיול פעם אחת, ולערוך את הדירוג שלו
    public RatingDTO addOrUpdateRating(Users user, Long tripId, Integer stars, String review) throws IOException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("הטיול לא נמצא"));

        Rating rating = ratingRepository.findByUser_IdAndTrip_Id(user.getId(), tripId)
                .orElseGet(Rating::new);

        rating.setStars(stars);
        rating.setReview(review);
        rating.setDate(LocalDate.now());
        rating.setUser(user);
        rating.setTrip(trip);

        Rating saved = ratingRepository.save(rating);
        return ratingMapper.ratingToDto(saved);
    }

    public void deleteRating(Users user, Long tripId) {
        Rating rating = ratingRepository.findByUser_IdAndTrip_Id(user.getId(), tripId)
                .orElseThrow(() -> new RuntimeException("לא נמצא דירוג למחיקה"));
        ratingRepository.deleteByUser_IdAndTrip_Id(user.getId(), tripId);
    }

    public java.util.List<RatingDTO> getRatingsByTrip(Long tripId) {
        return ratingMapper.ratingsListToDto(ratingRepository.getRatingsByTrip_IdOrderByDateDesc(tripId));
    }

    public RatingDTO getMyRatingForTrip(Users user, Long tripId) throws IOException {
        Rating rating = ratingRepository.findByUser_IdAndTrip_Id(user.getId(), tripId).orElse(null);
        if (rating == null) {
            return null;
        }
        return ratingMapper.ratingToDto(rating);
    }

    public RatingSummaryDTO getRatingSummary(Long tripId) {
        Double avg = ratingRepository.getAverageRatingForTrip(tripId);
        long count = ratingRepository.countByTrip_Id(tripId);
        double rounded = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
        return new RatingSummaryDTO(rounded, count);
    }
}