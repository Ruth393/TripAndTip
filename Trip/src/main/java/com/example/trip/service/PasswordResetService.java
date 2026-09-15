package com.example.trip.service;

import com.example.trip.model.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final int TOKEN_VALID_MINUTES = 30;
    private final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;


    public PasswordResetService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /** לא חושפים אם המייל קיים או לא - אם לא נמצא, פשוט לא קורה כלום. */
    public void requestReset(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return;
        }

        String rawToken = generateRawToken();
        user.setResetTokenHash(hash(rawToken));
        user.setResetTokenExpiry(Instant.now().plus(TOKEN_VALID_MINUTES, ChronoUnit.MINUTES));
        userRepository.save(user);

        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    /** @return true אם הסיסמה אופסה בהצלחה, false אם הטוקן לא תקין/פג תוקף */
    public boolean resetPassword(String rawToken, String newPassword) {
        String hashed = hash(rawToken);
        Optional<Users> userOpt = userRepository.findByResetTokenHash(hashed);
        if (userOpt.isEmpty()) {
            return false;
        }

        Users user = userOpt.get();
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(Instant.now())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetTokenHash(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}