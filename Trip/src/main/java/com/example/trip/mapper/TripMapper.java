package com.example.trip.mapper;

import com.example.trip.dto.ImageDTO;
import com.example.trip.dto.TripDTO;
import com.example.trip.dto.TripListDTO;
import com.example.trip.dto.TripLocationDTO;
import com.example.trip.dto.UserToSeeDTO;
import com.example.trip.model.Trip;
import com.example.trip.model.TripImage;
import com.example.trip.model.Users;
import com.example.trip.service.ImageUtils;
import org.mapstruct.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TripMapper {
    UserToSeeDTO userToSeeDTO(Users user);

    List<TripListDTO> tripsListToDto(List<Trip> trips);

    default TripLocationDTO tripLocationToDto(com.example.trip.model.TripLocation loc) {
        if (loc == null) return null;
        TripLocationDTO dto = new TripLocationDTO();
        dto.setLabel(loc.getLabel());
        dto.setLatitude(loc.getLatitude());
        dto.setLongitude(loc.getLongitude());
        dto.setCategory(loc.getCategory());
        dto.setDescription(loc.getDescription());
        dto.setType(loc.getType());
        return dto;
    }

    default List<TripLocationDTO> wrapLocation(com.example.trip.model.TripLocation loc) {
        if (loc == null) return java.util.Collections.emptyList();
        return java.util.List.of(tripLocationToDto(loc));
    }

    // ממיר את רשימת ה-TripImage של הטיול לרשימת ImageDTO (עם Base64)
    default List<ImageDTO> tripImagesToDto(List<TripImage> images) {
        List<ImageDTO> result = new ArrayList<>();
        if (images == null) return result;
        for (TripImage ti : images) {
            try {
                result.add(new ImageDTO(ti.getId(), ImageUtils.getImage(ti.getImagePath())));
            } catch (IOException e) {
                // מדלגים על תמונה שלא נמצאה בדיסק, לא מפילים את כל הטיול
            }
        }
        return result;
    }

    default TripDTO tripToDto(Trip t) throws IOException {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(t.getId());
        tripDTO.setName(t.getName());
        tripDTO.setDescription(t.getDescription());
        tripDTO.setCost(t.getCost());
        tripDTO.setMatch(t.getMatch());
        tripDTO.setDifficulty(t.getDifficulty() != null ? t.getDifficulty().name() : null);
        tripDTO.setKidFriendly(t.getKidFriendly());
        tripDTO.setCostAmount(t.getCostAmount());
        tripDTO.setImagePath(t.getImagePath());
        tripDTO.setLocations(wrapLocation(t.getLocation()));

        if (t.getUser() != null) {
            tripDTO.setUser(userToSeeDTO(t.getUser()));
        } else {
            tripDTO.setUser(null);
        }

        tripDTO.setCategory(t.getCategory());
        tripDTO.setComments(t.getComments());

        // תמונה ראשית - לתאימות לאחור
        if (t.getImagePath() != null && !t.getImagePath().trim().isEmpty()) {
            try {
                tripDTO.setImage(ImageUtils.getImage(t.getImagePath()));
            } catch (IOException e) {
                tripDTO.setImage(null);
            }
        } else {
            tripDTO.setImage(null);
        }

        // כל התמונות של הטיול (חדש)
        tripDTO.setImages(tripImagesToDto(t.getImages()));

        if (t.getUser() != null) {
            if (t.getUser().getImagePath() != null && !t.getUser().getImagePath().trim().isEmpty()) {
                try {
                    tripDTO.getUser().setImage(ImageUtils.getImage(t.getUser().getImagePath()));
                } catch (IOException e) {
                    tripDTO.getUser().setImage(null);
                }
            } else {
                if (tripDTO.getUser() != null) {
                    tripDTO.getUser().setImage(null);
                }
            }
        }

        return tripDTO;
    }

    default TripListDTO tripListToDto(Trip t) throws IOException {
        TripListDTO dto = new TripListDTO();

        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());

        dto.setDifficulty(t.getDifficulty() != null ? t.getDifficulty().name() : null);
        dto.setKidFriendly(t.getKidFriendly());
        dto.setCostAmount(t.getCostAmount());
        dto.setImagePath(t.getImagePath());

        if (t.getUser() != null) {
            dto.setUser(userToSeeDTO(t.getUser()));
        } else {
            dto.setUser(null);
        }

        if (t.getImagePath() != null && !t.getImagePath().trim().isEmpty()) {
            try {
                dto.setImage(ImageUtils.getImage(t.getImagePath()));
            } catch (IOException e) {
                dto.setImage(null);
            }
        } else {
            dto.setImage(null);
        }

        if (t.getUser() != null) {
            if (t.getUser().getImagePath() != null && !t.getUser().getImagePath().trim().isEmpty()) {
                try {
                    dto.getUser().setImage(ImageUtils.getImage(t.getUser().getImagePath()));
                } catch (IOException e) {
                    dto.getUser().setImage(null);
                }
            } else {
                if (dto.getUser() != null) {
                    dto.getUser().setImage(null);
                }
            }
        }

        return dto;
    }
}