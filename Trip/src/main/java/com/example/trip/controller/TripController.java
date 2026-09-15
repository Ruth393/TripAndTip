package com.example.trip.controller;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import com.example.trip.dto.ChatRequest;
import com.example.trip.dto.TripDTO;
import com.example.trip.dto.TripListDTO;
import com.example.trip.mapper.TripMapper;
import com.example.trip.model.Trip;
import com.example.trip.model.TripImage;
import com.example.trip.model.Users;
import com.example.trip.security.CustomUserDetails;
import com.example.trip.service.AIChatService;
import com.example.trip.service.CommentRepository;
import com.example.trip.service.FavoriteRepository;
import com.example.trip.service.ImageUtils;
import com.example.trip.service.RatingRepository;
import com.example.trip.service.TripRepository;
import com.example.trip.service.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin
public class TripController {
    private AIChatService aIChatService;
    private TripRepository tripRepository;
    private TripMapper tripMapper;
    private UserRepository userRepository;
    private FavoriteRepository favoriteRepository;
    private RatingRepository ratingRepository;
    private CommentRepository commentRepository;

    @Autowired
    public TripController(TripRepository tripRepository,
                          TripMapper tripMapper,
                          AIChatService aIChatService,
                          UserRepository userRepository,
                          FavoriteRepository favoriteRepository,
                          RatingRepository ratingRepository,
                          CommentRepository commentRepository) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.aIChatService = aIChatService;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripListDTO>> getTrips() {
        try {
            List<Trip> trips = tripRepository.findAll();
            List<TripListDTO> tripListDTOs = tripMapper.tripsListToDto(trips);
            return new ResponseEntity<>(tripListDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getTripById/{id}")
    public ResponseEntity<TripDTO> get(@PathVariable long id) throws IOException {
        return tripRepository.findByIdWithComments(id)
                .map(t -> {
                    tripRepository.findByIdWithImages(id).ifPresent(withImages ->
                            t.setImages(withImages.getImages())
                    );
                    try {
                        return new ResponseEntity<>(tripMapper.tripToDto(t), HttpStatus.OK);
                    } catch (IOException e) {
                        return new ResponseEntity<TripDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/tripsByUserId/{id}")
    public ResponseEntity<List<TripListDTO>> getTripsByUserId(@PathVariable long id) {
        try {
            List<Trip> trips = tripRepository.getTripsByUser_Id(id);
            if (trips.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(tripMapper.tripsListToDto(trips), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tripsByCategoryId/{id}")
    public ResponseEntity<List<TripListDTO>> getTripsByCategoryId(@PathVariable long id) {
        try {
            List<Trip> trips = tripRepository.getTripsByCategory_Id(id);
            if (trips.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(tripMapper.tripsListToDto(trips), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/uploadTrip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTripWithImage(
            @RequestPart("images") List<MultipartFile> files,
            @RequestPart("trip") Trip t) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users currentUser = userRepository.findByEmail(userDetails.getEmail());

            if (currentUser == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            if (files == null || files.isEmpty()) {
                return new ResponseEntity<>("נדרשת לפחות תמונה אחת", HttpStatus.BAD_REQUEST);
            }
            t.setUser(currentUser);

            List<String> savedFileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                savedFileNames.add(ImageUtils.uploadImage(file));
            }

            t.setImagePath(savedFileNames.get(0));

            Trip savedTrip = tripRepository.save(t);

            List<TripImage> tripImages = new ArrayList<>();
            for (String fileName : savedFileNames) {
                tripImages.add(new TripImage(fileName, savedTrip));
            }
            savedTrip.setImages(tripImages);
            tripRepository.save(savedTrip);

            return new ResponseEntity<>(tripMapper.tripToDto(savedTrip), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/deleteTripByAdmin/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteTripByAdmin(@PathVariable Long id) {
        if (!tripRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("הטיול לא נמצא");
        }
        try {
            favoriteRepository.deleteByTrip_Id(id);
            ratingRepository.deleteByTrip_Id(id);
            commentRepository.deleteByTrip_Id(id);
            tripRepository.deleteById(id);
            return ResponseEntity.ok("הטיול נמחק בהצלחה על ידי מנהל המערכת");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("שגיאה במחיקת הטיול");
        }
    }

    @GetMapping("/admin/dashboard-stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        long totalTrips = tripRepository.count();
        long totalUsers = userRepository.count();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalTrips", totalTrips);
        stats.put("totalUsers", totalUsers);

        return ResponseEntity.ok(stats);
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> chat(@RequestBody ChatRequest chatRequest) {
        Flux<String> response = aIChatService.getResponse2(
                        chatRequest.message(),
                        chatRequest.conversationId() != null ? chatRequest.conversationId() : "default"
                )
                .map(chunk -> "data: " + chunk + "\n\n");

        return ResponseEntity.ok()
                .header("X-Accel-Buffering", "no")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripListDTO>> searchTrips(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Boolean kidFriendly,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost,
            @RequestParam(required = false) String keyword
    ) {
        try {
            com.example.trip.model.Difficulty diffEnum = null;
            if (difficulty != null && !difficulty.isBlank()) {
                diffEnum = com.example.trip.model.Difficulty.valueOf(difficulty.toUpperCase());
            }

            List<Trip> trips = tripRepository.findAll(
                    com.example.trip.service.TripSpecification.withFilters(
                            categoryId, diffEnum, kidFriendly, minCost, maxCost, keyword
                    )
            );

            return new ResponseEntity<>(tripMapper.tripsListToDto(trips), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}