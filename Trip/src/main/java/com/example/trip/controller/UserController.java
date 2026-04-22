package com.example.trip.controller;

import com.example.trip.dto.SignInDTO;
import com.example.trip.mapper.UserMapper;
import com.example.trip.model.Users;
import com.example.trip.security.CustomUserDetails;
import com.example.trip.security.jwt.JwtUtils;
import com.example.trip.service.ImageUtils;
import com.example.trip.service.RoleRepository;
import com.example.trip.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository,
                          AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    // ─── GET /me ───────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) return ResponseEntity.status(404).body("User not found");
        return ResponseEntity.ok(userMapper.toSignInDTO(user));
    }

    // ─── GET /users ────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<List<Users>> getUsers() {
        try {
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─── POST /signIn ──────────────────────────────────────────
    // גוף הבקשה: { "email": "...", "password": "..." }
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Users u) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails d
                && d.getUsername().equals(u.getEmail())) {
            return ResponseEntity.ok("היי, אתה כבר מחובר! 😊");
        }

        // חיפוש לפי אימייל
        Users user = userRepository.findByEmail(u.getEmail());
        if (user == null) return ResponseEntity.status(404).body("משתמש לא נמצא");

        try {
            // ✅ מאמת לפי userName (מה ש-Spring Security מכיר) + password
            var a = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(u.getEmail(), u.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(a);
            var cookie = jwtUtils.generateJwtCookie((CustomUserDetails) a.getPrincipal());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(userMapper.toSignInDTO(user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("סיסמה שגויה");
        }
    }

    // ─── POST /signUp ──────────────────────────────────────────
    @PostMapping(value = "/signUp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signUp(@RequestPart("image") MultipartFile file,
                                    @RequestPart("user") Users user) throws IOException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("האימייל כבר קיים במערכת");
        }
        if (userRepository.findByUserName(user.getUserName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("שם המשתמש כבר קיים במערכת");
        }

        ImageUtils.uploadImage(file);
        user.setImagePath(file.getOriginalFilename());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.getRoles().add(roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Role USER not found")));

        userRepository.save(user);
        return new ResponseEntity<>(userMapper.toSignInDTO(user), HttpStatus.CREATED);
    }

    // ─── PUT /update ───────────────────────────────────────────
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "userName", required = false) String userName) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails d)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("לא מחובר");
        }

        Users user = userRepository.findByEmail(d.getUsername());
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("משתמש לא נמצא");

        try {
            if (image != null && !image.isEmpty()) {
                ImageUtils.uploadImage(image);
                user.setImagePath(image.getOriginalFilename());
            }

            if (userName != null && !userName.isBlank()) {
                if (userRepository.findByUserName(userName) != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("שם המשתמש כבר תפוס");
                }
                user.setUserName(userName);
            }

            userRepository.save(user);
            return ResponseEntity.ok(userMapper.toSignInDTO(user));

        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("שגיאה בקריאת קובץ");
        }
    }

    // ─── POST /signOut ─────────────────────────────────────────
    @PostMapping("/signOut")
    public ResponseEntity<?> signOut() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("you've been signed out!");
    }
}