package com.example.trip.service;

import com.example.trip.dto.TripDTO;
import com.example.trip.dto.TripListDTO;
import com.example.trip.mapper.TripMapper;
import com.example.trip.model.Difficulty;
import com.example.trip.model.Trip;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class TripTools {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public TripTools(TripRepository tripRepository, TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
    }

    @Tool(description = """
        חיפוש טיולים במערכת לפי פילטרים.
        ניתן לסנן לפי מילת חיפוש (keyword), רמת קושי (difficulty),
        התאמה לילדים (kidFriendly), מחיר מינימלי (minCost) ומחיר מקסימלי (maxCost).
        השאר כל פרמטר לא רלוונטי כ-null.
        השתמש בכלי הזה כאשר המשתמש מבקש למצוא טיול, יעד, או המלצה מתוך המערכת -
        אל תמציא טיולים שלא הוחזרו מהכלי הזה.
        """)
    public List<TripListDTO> searchTrips(String keyword,
                                         String difficulty,
                                         Boolean kidFriendly,
                                         Double minCost,
                                         Double maxCost) {

        Difficulty diffEnum = null;
        if (difficulty != null && !difficulty.isBlank()) {
            try {
                diffEnum = Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // אם ה-AI ניחש ערך לא תקין, פשוט מתעלמים מהפילטר הזה
            }
        }

        List<Trip> trips = tripRepository.findAll(
                TripSpecification.withFilters(null, diffEnum, kidFriendly, minCost, maxCost, keyword)
        );

        return tripMapper.tripsListToDto(trips);
    }

    @Tool(description = """
        קבלת פרטים מלאים על טיול ספציפי לפי מזהה (id) - כולל תיאור, מחיר,
        רמת קושי ומיקום עם קואורדינטות (latitude/longitude).
        השתמש בכלי הזה אחרי שכבר יש לך id של טיול (למשל מתוצאות searchTrips)
        וברצונך לבדוק את מזג האוויר במיקום שלו.
        """)
    public TripDTO getTripDetails(Long id) {
        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) return null;
        try {
            return tripMapper.tripToDto(tripOpt.get());
        } catch (IOException e) {
            return null;
        }
    }
}