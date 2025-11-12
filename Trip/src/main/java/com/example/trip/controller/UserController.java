package com.example.trip.controller;

import com.example.trip.dto.SignInDTO;
import com.example.trip.dto.SignUpDTO;
import com.example.trip.mapper.UserMapper;
import com.example.trip.model.Users;
import com.example.trip.service.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/users")
@RestController
@CrossOrigin
public class UserController {
    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getUsers(){
        try {
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/signUp")
    public ResponseEntity<Users> signUp(@RequestBody SignUpDTO u) {
        try {
            Users u1 = userRepository.findByName(u.getName());
            if (u1 != null) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
            Users newUser = userMapper.toSignUpDTO(u);
            return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/signIn")
    public ResponseEntity<Users> signIn(@RequestBody SignInDTO u) {
        try {
            if (u.getName() == null || u.getName().trim().isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Users u1=userRepository.findByName(u.getName());
            if(u1==null){
                return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
            }
            if(u1.getEmail().equals(u.getEmail())){
                return new ResponseEntity<>(u1,HttpStatus.OK);
            }
            return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
