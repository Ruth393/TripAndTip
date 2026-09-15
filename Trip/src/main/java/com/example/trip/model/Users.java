package com.example.trip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "נדרש שם משתמש")
    private String userName;

    @NotBlank(message = "נדרשת כתובת האימייל")
    @Email(message = "פורמט האימייל אינו תקין")
    private String email;

    @NotBlank
    @JsonIgnore
    @Size(min = 8, max = 255, message = "הסיסמה חייבת להיות בת 8 תווים")
    private String password;

    private String image;

    @Size(max = 255, message = "נתיב התמונה ארוך מדי")
    private String imagePath;

    // ─── חדש: כתובת תמונת הפרופיל שמגיעה מ-Google OAuth (שדה ה-picture) ───
    // לא מתערבב עם imagePath בכוונה: imagePath משמש רק לקבצים מקומיים
    // ש-ImageUtils.getImage() קורא מהדיסק, בעוד ש-googleImageUrl הוא URL חיצוני
    // מלא (https://...) שמוצג ישירות ב-Frontend בלי הורדה/שמירה מקומית.
    @Size(max = 500, message = "כתובת תמונת גוגל ארוכה מדי")
    private String googleImageUrl;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Trip> trips;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Comment> comments;

    @ManyToMany
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    @jakarta.validation.constraints.Size(max = 500, message = "הביו ארוך מדי (עד 500 תווים)")
    private String bio;

    // ─── חדש: איפוס סיסמה - שומרים רק hash של הטוקן, לא את הטוקן עצמו ───
    @JsonIgnore
    private String resetTokenHash;

    @JsonIgnore
    private Instant resetTokenExpiry;

    public Users() {}

    public Users(String bio, List<Comment> comments, String email, Long id, String image, String imagePath, String password, Set<Role> roles, List<Trip> trips, String userName) {
        this.bio = bio;
        this.comments = comments;
        this.email = email;
        this.id = id;
        this.image = image;
        this.imagePath = imagePath;
        this.password = password;
        this.roles = roles;
        this.trips = trips;
        this.userName = userName;
    }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Trip> getTrips() { return trips; }
    public void setTrips(List<Trip> trips) { this.trips = trips; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }


    public String getGoogleImageUrl() { return googleImageUrl; }
    public void setGoogleImageUrl(String googleImageUrl) { this.googleImageUrl = googleImageUrl; }


    public String getResetTokenHash() { return resetTokenHash; }
    public void setResetTokenHash(String resetTokenHash) { this.resetTokenHash = resetTokenHash; }
    public Instant getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(Instant resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }
}