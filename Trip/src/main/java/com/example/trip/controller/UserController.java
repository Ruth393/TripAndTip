package com.example.trip.controller;

import com.example.trip.dto.SignInDTO;
import com.example.trip.mapper.UserMapper;
import com.example.trip.model.ERole;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;

import com.example.trip.security.CustomUserDetails;
import com.example.trip.security.jwt.JwtUtils;
import com.example.trip.service.ImageUtils;
import com.example.trip.service.RoleRepository;
import com.example.trip.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.trip.service.ImageUtils;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private UserMapper userMapper;


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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Users user = userRepository.findByUserName(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        SignInDTO dto = userMapper.toSignInDTO(user);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getUsers(){
        try {
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Users u) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails d
                && d.getUsername().equals(u.getUserName())) {
            return ResponseEntity.ok("היי " + u.getUserName() + ", אתה כבר מחובר! 😊");
        }

        Users user = userRepository.findByUserName(u.getUserName());
        if (user == null) return ResponseEntity.status(404).body("משתמש לא נמצא");

        try {
            var a = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(u.getUserName(), u.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(a);

            var cookie = jwtUtils.generateJwtCookie((CustomUserDetails) a.getPrincipal());
            var res = userMapper.toSignInDTO(user);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(res);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("סיסמה שגויה");
        }
    }
    @PostMapping(value = "/signUp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signUp(@RequestPart("image") MultipartFile file, @RequestPart("user") Users user) throws IOException {
        if (userRepository.findByUserName(user.getUserName()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("המשתמש כבר קיים במערכת");
        }
        ImageUtils.uploadImage(file);
        user.setImagePath(file.getOriginalFilename());
            String ADMIN_SECRET = "Admin2025";
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.getRoles().add(roleRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Role not found")));
            if (ADMIN_SECRET.equals(user.getPassword())) {
                user.getRoles().add(roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found")));
            }
            userRepository.save(user);
            SignInDTO response = userMapper.toSignInDTO(user);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

    @PostMapping("/signOut")
    public ResponseEntity<?> signOut(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("you've been signed out!");
    }
}

