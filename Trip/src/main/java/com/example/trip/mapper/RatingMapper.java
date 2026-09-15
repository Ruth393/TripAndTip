package com.example.trip.mapper;

import com.example.trip.dto.RatingDTO;
import com.example.trip.dto.UserToSeeDTO;
import com.example.trip.model.Rating;
import com.example.trip.model.Users;
import com.example.trip.service.ImageUtils;
import org.mapstruct.Mapper;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    UserToSeeDTO userToSeeDTO(Users user);

    List<RatingDTO> ratingsListToDto(List<Rating> ratings);

    default RatingDTO ratingToDto(Rating r) throws IOException {
        RatingDTO dto = new RatingDTO();
        dto.setId(r.getId());
        dto.setStars(r.getStars());
        dto.setReview(r.getReview());
        dto.setDate(r.getDate());
        dto.setUser(userToSeeDTO(r.getUser()));

        if (r.getUser() != null && r.getUser().getImagePath() != null && !r.getUser().getImagePath().trim().isEmpty()) {
            try {
                dto.getUser().setImage(ImageUtils.getImage(r.getUser().getImagePath()));
            } catch (IOException e) {
                dto.getUser().setImage(null);
            }
        }
        return dto;
    }
}