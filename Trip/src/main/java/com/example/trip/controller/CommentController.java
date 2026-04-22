package com.example.trip.controller;

import com.example.trip.dto.CommentDTO;
import com.example.trip.dto.CommentToAddDTO;
import com.example.trip.mapper.CommentMapper;
import com.example.trip.model.Comment;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;
import com.example.trip.service.UserRepository;
import com.example.trip.service.TripRepository;
import com.example.trip.service.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RequestMapping("api/comment")
@RestController
@CrossOrigin
public class CommentController {
    private final CommentMapper commentMapper;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private  TripRepository tripRepository;

    public CommentController(CommentRepository commentRepository, CommentMapper commentMapper, UserRepository userRepository, TripRepository tripRepository) {
        this.commentRepository=commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository=userRepository;
        this.tripRepository=tripRepository;
    }
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getComments(){
        try {

            return new ResponseEntity<>(commentRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addComment")
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentToAddDTO commentToAddDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Users currentUser = userRepository.findByUserName(username);

            if (currentUser == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            if (commentToAddDTO.getTrip() == null || commentToAddDTO.getTrip().getId() == null) {
                System.out.println("ERROR: Trips is null or Trips ID is null");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Trip trip = tripRepository.findById(commentToAddDTO.getTrip().getId())
                    .orElseThrow(() -> new RuntimeException("Trip not found"));
            Comment comment = commentMapper.toComment(commentToAddDTO);
            comment.setUser(currentUser);
            comment.setTrip(trip);
            Comment savedComment = commentRepository.save(comment);
            return new ResponseEntity<>(commentMapper.commentToDto(savedComment), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/getCommentsByTripsId/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTripsId(@PathVariable long id){
        try {
            List<Comment> comments = commentRepository.getCommentsByTrip_Id(id);
            if (comments.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return  new ResponseEntity<>(commentMapper.commentsListDTO(comments),HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
