package com.example.trip.security;

import com.example.trip.model.ERole;
import com.example.trip.model.Role;
import com.example.trip.model.Users;
import com.example.trip.security.jwt.JwtUtils;
import com.example.trip.service.RoleRepository;
import com.example.trip.service.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, RoleRepository roleRepository,
                                     JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        // ─── חדש: תמונת הפרופיל שמגיעה מגוגל (דורש scope "profile", שכבר מוגדר) ───
        String picture = oAuth2User.getAttribute("picture");

        if (email == null) {
            response.sendRedirect(frontendUrl + "/sign-in?error=google");
            return;
        }

        Users user = userRepository.findByEmailWithRoles(email).orElse(null);

        if (user == null) {
            user = new Users();
            user.setEmail(email);
            user.setUserName(generateUniqueUserName(name != null ? name : email));
            // סיסמה אקראית מוצפנת - המשתמש הזה מתחבר תמיד דרך גוגל, לא איתה
            user.setPassword(passwordEncoder.encode(generateRandomPassword()));
            user.getRoles().add(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role USER not found")));
            // ─── חדש: שמירת תמונת גוגל למשתמש חדש. imagePath נשאר null בכוונה -
            // הוא מיועד רק לתמונות שהועלו ידנית ונשמרות כקובץ מקומי ───
            if (picture != null && !picture.isBlank()) {
                user.setGoogleImageUrl(picture);
            }
            userRepository.save(user);
        } else {
            // ─── חדש: משתמש Google קיים - מעדכנים googleImageUrl אם גוגל החזירה picture חדש,
            // בלי לגעת ב-imagePath/image הקיימים שלו (אם למשל הוא גם העלה תמונה ידנית בעבר) ───
            if (picture != null && !picture.isBlank() && !Objects.equals(picture, user.getGoogleImageUrl())) {
                user.setGoogleImageUrl(picture);
                userRepository.save(user);
            }
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        CustomUserDetails userDetails = new CustomUserDetails(user.getEmail(), user.getPassword(), authorities);

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(frontendUrl + "/auth/callback");
    }

    private String generateUniqueUserName(String base) {
        String cleanBase = base.replaceAll("[^a-zA-Z0-9א-ת]", "").trim();
        if (cleanBase.isEmpty()) cleanBase = "user";
        String candidate = cleanBase;
        int suffix = 1;
        while (userRepository.findByUserName(candidate) != null) {
            candidate = cleanBase + suffix++;
        }
        return candidate;
    }

    private String generateRandomPassword() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}