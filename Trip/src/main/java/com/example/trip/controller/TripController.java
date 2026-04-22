package com.example.trip.controller;

import com.example.trip.dto.ChatRequest;
import com.example.trip.dto.TripDTO;
import com.example.trip.dto.TripListDTO;
import com.example.trip.mapper.TripMapper;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;
import com.example.trip.service.AIChatService;
import com.example.trip.service.ImageUtils;
import com.example.trip.service.TripRepository;
import com.example.trip.service.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin
public class TripController {
    private  AIChatService aIChatService;
    private  TripRepository tripRepository;
    private  TripMapper tripMapper;
    private  UserRepository userRepository;

    @Autowired
    public TripController(TripRepository tripRepository,
                          TripMapper tripMapper,
                          AIChatService aIChatService,
                          UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.aIChatService = aIChatService;
        this.userRepository = userRepository;
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
        return tripRepository.findById(id)
                .map(t -> {
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
    public ResponseEntity<TripDTO> uploadTripWithImage(@RequestPart("image") MultipartFile file, @RequestPart("trip") Trip t) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users currentUser = userRepository.findByUserName(userDetails.getUsername());

            if (currentUser == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            t.setUser(currentUser);

            ImageUtils.uploadImage(file);
            t.setImagePath(file.getOriginalFilename());
            Trip trip = tripRepository.save(t);
            return new ResponseEntity<>(tripMapper.tripToDto(trip), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/packingList/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getPackingList(@PathVariable long id) {
        Flux<String> packingList = aIChatService.getPackingListForTrip(id)
                .map(chunk -> "data: " + chunk + "\n\n");

        return ResponseEntity.ok()
                .header("X-Accel-Buffering", "no")
                .header("Cache-Control", "no-cache")
                .body(packingList);
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
}