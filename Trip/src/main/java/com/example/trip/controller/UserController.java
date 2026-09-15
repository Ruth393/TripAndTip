package com.example.trip.controller;

import com.example.trip.dto.SignInDTO;
import com.example.trip.dto.UserProfileDTO;
import com.example.trip.mapper.UserMapper;
import com.example.trip.model.ERole;
import com.example.trip.model.Role;
import com.example.trip.model.Users;
import com.example.trip.security.CustomUserDetails;
import com.example.trip.security.jwt.JwtUtils;
import com.example.trip.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.trip.dto.ForgotPasswordDTO;
import com.example.trip.dto.ResetPasswordDTO;
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


    private final TripRepository tripRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final PasswordResetService passwordResetService;
    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository,
                          AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserMapper userMapper,
                          TripRepository tripRepository, FavoriteRepository favoriteRepository, RatingRepository ratingRepository, PasswordResetService passwordResetService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.tripRepository = tripRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.passwordResetService = passwordResetService;
    }

    // ─── GET /me ───────────────────────────────────────────────
    // תוקן: שימוש ב-findByEmailWithRoles כדי לטעון roles בתוך אותה טרנזקציה
    // ולמנוע LazyInitializationException כשה-Mapper ניגש ל-getRoles()
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailWithRoles(userDetails.getUsername())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
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
    // תוקן: שימוש ב-findByEmailWithRoles כדי לטעון roles בתוך אותה טרנזקציה
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Users u) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails d
                && d.getUsername().equalsIgnoreCase(u.getEmail())) {
            return ResponseEntity.ok("היי, אתה כבר מחובר! 😊");
        }

        Users user = userRepository.findByEmailWithRoles(u.getEmail())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("משתמש לא נמצא");
        }

        try {
            Authentication a = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(u.getEmail(), u.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(a);
            ResponseCookie cookie = jwtUtils.generateJwtCookie((CustomUserDetails) a.getPrincipal());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(userMapper.toSignInDTO(user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("סיסמה שגויה");
        }
    }

    // ─── POST /signUp ──────────────────────────────────────────
    // תוקן: הוספת הגדרת MediaType מפורשת ל-user כדי למנוע שגיאות המרה באנגולר
    // הערה: כאן אין בעיית LazyInitialization כי ה-user הוא אובייקט חדש
    // וה-roles מתווספים אליו בזיכרון לפני השמירה
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

        user.getRoles().add(roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found")));

        userRepository.save(user);
        return new ResponseEntity<>(userMapper.toSignInDTO(user), HttpStatus.CREATED);
    }

    // ─── PUT /update ───────────────────────────────────────────
    // תוקן: שימוש ב-findByEmailWithRoles כדי לטעון roles בתוך אותה טרנזקציה
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "userName", required = false) String userName,
            @RequestPart(value = "bio", required = false) String bio) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails d)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("לא מחובר");
        }

        Users user = userRepository.findByEmailWithRoles(d.getUsername())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("משתמש לא נמצא");
        }

        try {
            if (image != null && !image.isEmpty()) {
                ImageUtils.uploadImage(image);
                user.setImagePath(image.getOriginalFilename());
            }

            if (userName != null && !userName.trim().isEmpty()) {
                // ניקוי גרשיים מיותרים שעלולים להגיע מאנגולר כתוצאה ממעבר ב-FormData
                String cleanUserName = userName.replace("\"", "").trim();

                if (!cleanUserName.equals(user.getUserName()) && userRepository.findByUserName(cleanUserName) != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("שם המשתמש כבר תפוס");
                }
                user.setUserName(cleanUserName);
            }

            if (bio != null) {
                user.setBio(bio.replace("\"", "").trim());
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

    // ─── POST /forgot-password ──────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok().build(); // תמיד 200, בלי לחשוף אם המייל קיים
    }

    // ─── POST /reset-password ───────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        boolean success = passwordResetService.resetPassword(request.getToken(), request.getPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("הקישור אינו תקין או שפג תוקפו");
        }
        return ResponseEntity.ok().build();
    }
    // ─── PUT /changeRole/{userId} ───────────────────────────────────────
// פונקציה המאפשרת למנהל לשנות תפקיד של משתמש אחר (להפוך אותו למנהל או להחזיר למשתמש רגיל)
    @PutMapping("/changeRole/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> changeUserRole(@PathVariable Long userId, @RequestParam ERole roleName) {
        return userRepository.findById(userId).map(user -> {
            Role targetRole = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("התפקיד המבוקש לא קיים במערכת"));

            // עדכון הרולים של המשתמש
            user.getRoles().clear(); // מנקים את התפקידים הקודמים אם רוצים להחליף לגמרי
            user.getRoles().add(targetRole);

            // אם רוצים שכל מנהל יהיה גם משתמש רגיל, אפשר להוסיף:
            if (roleName == ERole.ROLE_ADMIN) {
                roleRepository.findByName(ERole.ROLE_USER).ifPresent(user.getRoles()::add);
            }

            userRepository.save(user);
            return ResponseEntity.ok("תפקיד המשתמש עודכן בהצלחה ל-" + roleName.name());
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("המשתמש לא נמצא"));
    }

    // ─── DELETE /banUser/{userId} ───────────────────────────────────────
// פונקציה חזקה למנהל - מחיקת משתמש לחלוטין מהמערכת (Ban)
    @DeleteMapping("/banUser/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("המשתמש לא נמצא");
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok("המשתמש נמחק לצמיתות מהמערכת על ידי המנהל");
    }
    // ─── GET /api/user/profile/me ─── הפרופיל שלי (מלא) ──────
    @GetMapping("/profile/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("לא מחובר");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailWithRoles(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("משתמש לא נמצא");
        }
        return ResponseEntity.ok(buildProfileDTO(user));
    }

    // ─── GET /api/user/profile/{id} ─── פרופיל ציבורי ──────
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        Users user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("משתמש לא נמצא");
        }
        return ResponseEntity.ok(buildProfileDTO(user));
    }

    private UserProfileDTO buildProfileDTO(Users user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setBio(user.getBio());
        dto.setImagePath(user.getImagePath());
        // ─── חדש ───
        dto.setGoogleImageUrl(user.getGoogleImageUrl());

        if (user.getImagePath() != null && !user.getImagePath().trim().isEmpty()) {
            try {
                dto.setImage(ImageUtils.getImage(user.getImagePath()));
            } catch (IOException e) {
                dto.setImage(null);
            }
        }

        dto.setTripsCount(tripRepository.countByUser_Id(user.getId()));
        dto.setTotalFavoritesReceived(favoriteRepository.countByTrip_User_Id(user.getId()));
        dto.setTotalRatingsReceived(ratingRepository.countByTrip_User_Id(user.getId()));

        Double avg = ratingRepository.getAverageRatingForUser(user.getId());
        dto.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null);

        return dto;
    }

}