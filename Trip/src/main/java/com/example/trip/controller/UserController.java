package com.example.trip.controller;

import com.example.trip.mapper.UserMapper;
import com.example.trip.model.ERole;
import com.example.trip.model.Users;

import com.example.trip.security.CustomUserDetails;
import com.example.trip.security.jwt.JwtUtils;
import com.example.trip.service.RoleRepository;
import com.example.trip.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private UserMapper userMapper; // **שדה נוסף**


    @Autowired
    public UserController(UserRepository userRepository,RoleRepository roleRepository ,AuthenticationManager authenticationManager,JwtUtils jwtUtils, UserMapper userMapper) { // **ה-Mapper נוסף לקונסטרוקטור**
        this.userRepository = userRepository;
        this.roleRepository=roleRepository;
        this.authenticationManager=authenticationManager;
        this.jwtUtils=jwtUtils;
        this.userMapper = userMapper;
    }

    @GetMapping("/get")
    public String get(){
        return "hello";
    }





    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Users u) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(u.getUserName(), u.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("התחברת בהצלחה!");
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody Users user) {
        if (userRepository.findByUserName(user.getUserName()) != null) {
            return new ResponseEntity<>("משתמש כבר קיים", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        Role userRole = (Role) roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("לא נמצא תפקיד USER"));

        user.getRoles().add((com.example.trip.model.Role) userRole);
        userRepository.save(user);

        return new ResponseEntity<>("נרשמת בהצלחה כמשתמש רגיל", HttpStatus.CREATED);
    }
    @PostMapping("/signOut")
    public ResponseEntity<?> signOut(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("you've been signed out!");
    }
}

