package com.example.trip.controller;

import com.example.trip.dto.CommentDTO;
import com.example.trip.mapper.CommentMapper;
import com.example.trip.model.Comment;
import com.example.trip.service.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/comment")
@RestController
@CrossOrigin
public class CommentController {
    private final CommentMapper commentMapper;
    private CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository, CommentMapper commentMapper){
        this.commentRepository=commentRepository;
        this.commentMapper = commentMapper;
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
    public ResponseEntity<Comment> addComment(@RequestBody Comment c){
        try {
            return new ResponseEntity<>(commentRepository.save(c), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getCommentsByTripsId/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTripsId(@PathVariable long id){
        try {
            List<Comment> comments = commentRepository.getCommentsByTrips_Id(id);
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
