package com.example.trip.controller;

import com.example.trip.dto.FavoriteDTO;
import com.example.trip.model.Users;
import com.example.trip.security.CustomUserDetails;
import com.example.trip.service.FavoriteService;
import com.example.trip.service.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteService favoriteService, UserRepository userRepository) {
        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
    }

    private Users getCurrentUser() {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getEmail());
    }

    // ─── POST /api/favorite/add/{tripId} ──────────────────────────
    @PostMapping("/add/{tripId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long tripId) {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>("משתמש לא מזוהה", HttpStatus.UNAUTHORIZED);
            }
            FavoriteDTO dto = favoriteService.addFavorite(currentUser, tripId);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("שגיאה בעיבוד נתוני הטיול", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── DELETE /api/favorite/remove/{tripId} ─────────────────────
    @DeleteMapping("/remove/{tripId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long tripId) {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>("משתמש לא מזוהה", HttpStatus.UNAUTHORIZED);
            }
            favoriteService.removeFavorite(currentUser, tripId);
            return ResponseEntity.ok("הוסר בהצלחה מהמועדפים");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── GET /api/favorite/myFavorites ─────────────────────────────
    @GetMapping("/myFavorites")
    public ResponseEntity<List<FavoriteDTO>> getMyFavorites() {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(favoriteService.getFavoritesByUser(currentUser), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── GET /api/favorite/isFavorite/{tripId} ─────────────────────
    @GetMapping("/isFavorite/{tripId}")
    public ResponseEntity<Map<String, Boolean>> isFavorite(@PathVariable Long tripId) {
        try {
            Users currentUser = getCurrentUser();
            if (currentUser == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            boolean fav = favoriteService.isFavorite(currentUser, tripId);
            return new ResponseEntity<>(Map.of("isFavorite", fav), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── GET /api/favorite/count/{tripId} ──────────────────────────
    // כמה משתמשים סימנו טיול זה כמועדף (שימושי להצגת "❤️ 12" בכרטיס הטיול)
    @GetMapping("/count/{tripId}")
    public ResponseEntity<Map<String, Long>> countFavorites(@PathVariable Long tripId) {
        try {
            long count = favoriteService.countFavoritesForTrip(tripId);
            return new ResponseEntity<>(Map.of("count", count), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}