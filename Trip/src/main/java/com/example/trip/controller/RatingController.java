package com.example.trip.controller;

import com.example.trip.dto.RatingDTO;
import com.example.trip.dto.RatingRequestDTO;
import com.example.trip.dto.RatingSummaryDTO;
import com.example.trip.model.Users;
import com.example.trip.security.CustomUserDetails;
import com.example.trip.service.RatingService;
import com.example.trip.service.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rating")
@CrossOrigin
public class RatingController {

    private final RatingService ratingService;
    private final UserRepository userRepository;

    public RatingController(RatingService ratingService, UserRepository userRepository) {
        this.ratingService = ratingService;
        this.userRepository = userRepository;
    }

    private Users getCurrentUser() {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getEmail());
    }

    // ─── POST /api/rating/rate/{tripId} ─── הוספה/עדכון דירוג ──────
    @PostMapping("/rate/{tripId}")
    public ResponseEntity<?> rateTrip(@PathVariable Long tripId, @Valid @RequestBody RatingRequestDTO request) {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>("משתמש לא מזוהה", HttpStatus.UNAUTHORIZED);
            }
            RatingDTO dto = ratingService.addOrUpdateRating(currentUser, tripId, request.getStars(), request.getReview());
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("שגיאה בעיבוד נתוני הדירוג", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── DELETE /api/rating/delete/{tripId} ─────────────────────────
    @DeleteMapping("/delete/{tripId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long tripId) {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>("משתמש לא מזוהה", HttpStatus.UNAUTHORIZED);
            }
            ratingService.deleteRating(currentUser, tripId);
            return ResponseEntity.ok("הדירוג נמחק בהצלחה");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── GET /api/rating/byTrip/{tripId} ─── כל הביקורות לטיול ──────
    @GetMapping("/byTrip/{tripId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByTrip(@PathVariable Long tripId) {
        try {
            return new ResponseEntity<>(ratingService.getRatingsByTrip(tripId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── GET /api/rating/myRating/{tripId} ─── הדירוג שלי לטיול הזה ──
    @GetMapping("/myRating/{tripId}")
    public ResponseEntity<?> getMyRating(@PathVariable Long tripId) {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            RatingDTO dto = ratingService.getMyRatingForTrip(currentUser, tripId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── GET /api/rating/summary/{tripId} ─── ממוצע + כמות (ציבורי) ──
    @GetMapping("/summary/{tripId}")
    public ResponseEntity<RatingSummaryDTO> getSummary(@PathVariable Long tripId) {
        try {
            return new ResponseEntity<>(ratingService.getRatingSummary(tripId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}