package com.example.trip.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("איפוס סיסמה - Trip");
        message.setText(
                "היי,\n\n" +
                        "קיבלנו בקשה לאיפוס הסיסמה שלך.\n" +
                        "לחצי על הקישור הבא כדי לבחור סיסמה חדשה (בתוקף ל-30 דקות):\n\n" +
                        resetLink + "\n\n" +
                        "אם לא ביקשת את זה, אפשר להתעלם מהמייל."
        );
        mailSender.send(message);
    }
}