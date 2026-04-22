package com.example.trip.service;

import com.example.trip.model.Trip;
import com.example.trip.service.TripRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class AgentTools {

    private final TripRepository tripRepository;

    public AgentTools(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Tool(
            name = "getTripDetailsForPacking",
            description = "מחזיר פרטי טיול כדי שה-AI יוכל ליצור רשימת ציוד מתאימה"
    )
    public String getTripDetailsForPacking(long tripId) {

        Trip trip = tripRepository.findById(tripId).orElse(null);

        if (trip == null) {
            return "Trip not found";
        }

        String details = """
            פרטי הטיול:

            שם הטיול: %s
            תיאור: %s
            מידע נוסף: %s

            על סמך מידע זה צור רשימת ציוד.
            """.formatted(
                trip.getName(),
                trip.getDescription(),
                trip.getMatch()
        );

        System.out.println("Trip Details sent to AI:");
        System.out.println(details);

        return details;
    }
}