package com.example.trip.service;

import com.example.trip.dto.FavoriteDTO;
import com.example.trip.mapper.TripMapper;
import com.example.trip.model.Favorite;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           TripRepository tripRepository,
                           TripMapper tripMapper) {
        this.favoriteRepository = favoriteRepository;
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
    }

    public FavoriteDTO addFavorite(Users user, Long tripId) throws IOException {
        if (favoriteRepository.existsByUser_IdAndTrip_Id(user.getId(), tripId)) {
            throw new IllegalStateException("הטיול כבר נמצא ברשימת המועדפים שלך");
        }
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("הטיול לא נמצא"));

        Favorite favorite = new Favorite(user, trip);
        Favorite saved = favoriteRepository.save(favorite);
        return toDto(saved);
    }

    public void removeFavorite(Users user, Long tripId) {
        if (!favoriteRepository.existsByUser_IdAndTrip_Id(user.getId(), tripId)) {
            throw new RuntimeException("הטיול לא נמצא ברשימת המועדפים שלך");
        }
        favoriteRepository.deleteByUser_IdAndTrip_Id(user.getId(), tripId);
    }

    public List<FavoriteDTO> getFavoritesByUser(Users user) throws IOException {
        List<Favorite> favorites = favoriteRepository.findByUser_IdOrderByDateAddedDesc(user.getId());
        List<FavoriteDTO> dtos = new ArrayList<>();
        for (Favorite f : favorites) {
            dtos.add(toDto(f));
        }
        return dtos;
    }

    public boolean isFavorite(Users user, Long tripId) {
        return favoriteRepository.existsByUser_IdAndTrip_Id(user.getId(), tripId);
    }

    public long countFavoritesForTrip(Long tripId) {
        return favoriteRepository.countByTrip_Id(tripId);
    }

    private FavoriteDTO toDto(Favorite favorite) throws IOException {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId());
        dto.setDateAdded(favorite.getDateAdded());
        dto.setTrip(tripMapper.tripListToDto(favorite.getTrip()));
        return dto;
    }
}