package com.example.trip.controller;

import com.example.trip.dto.ChatRequest;
import com.example.trip.dto.TripDTO;
import com.example.trip.dto.TripListDTO;
import com.example.trip.mapper.TripMapper;
import com.example.trip.model.Trip;
import com.example.trip.service.AIChatService;
import com.example.trip.service.ImageUtils;
import com.example.trip.service.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin
public class TripController {
    private final AIChatService aIChatService;
    TripRepository tripRepository;
    TripMapper tripMapper;

    @Autowired
    public TripController(TripRepository tripRepository, TripMapper tripMapper, AIChatService aIChatService) {
        this.tripRepository = tripRepository;
        this.tripMapper=tripMapper;
        this.aIChatService = aIChatService;
    }
    @GetMapping("/trips")
    public ResponseEntity<List<TripListDTO>> getTrips() { // שינוי ל-List<TripListDTO>
        try {
            List<Trip> trips = tripRepository.findAll();
            List<TripListDTO> tripListDTOs = tripMapper.tripsListToDto(trips);
            return new ResponseEntity<>(tripListDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getTripById/{id}")
    public ResponseEntity<TripDTO> get(@PathVariable long id) throws IOException {
        Trip t=tripRepository.findById(id).get();
        if(t!=null)
            return new ResponseEntity<>(tripMapper.tripToDto(t),HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/tripsByUserId/{id}")
    public ResponseEntity<List<TripListDTO>> getTripsByUserId(@PathVariable long id){
        try {
            List<Trip> trips = tripRepository.getTripsByUser_Id(id);
            if (trips.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return  new ResponseEntity<>(tripMapper.tripsListToDto(trips),HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/tripsByCatgoriesId/{id}")
    public ResponseEntity<List<TripListDTO>> getTripsByCategoryId(@PathVariable long id){
        try {
            List<Trip> trips = tripRepository.getTripsByCategories_Id(id);
            if (trips.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return  new ResponseEntity<>(tripMapper.tripsListToDto(trips),HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/uploadTrip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TripDTO> uploadTripWithImage(@RequestPart("image") MultipartFile file, @RequestPart("trip") Trip t) {
        try{
            ImageUtils.uploadImage(file);
            t.setImagePath(file.getOriginalFilename());
            Trip trip=tripRepository.save(t);
            return new ResponseEntity<>(tripMapper.tripToDto(trip), HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/chat")
    public String getResponse(@RequestBody ChatRequest chatRequest){
        return aIChatService.getResponse2(chatRequest.message(),chatRequest.conversationId());
    }
}
