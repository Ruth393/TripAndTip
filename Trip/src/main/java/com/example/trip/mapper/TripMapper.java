package com.example.trip.mapper;

import com.example.trip.dto.TripDTO;
import com.example.trip.dto.TripListDTO;
import com.example.trip.dto.UserToSeeDTO;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;
import com.example.trip.service.ImageUtils;
import org.mapstruct.Mapper;
import java.io.IOException;
import java.util.List;

@Mapper(componentModel="spring")
public interface TripMapper {
    UserToSeeDTO userToSeeDTO(Users user);

    List<TripListDTO> tripsListToDto(List<Trip> trips);

    default TripDTO tripToDto(Trip t) throws IOException {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(t.getId());
        tripDTO.setName(t.getName());
        tripDTO.setDescription(t.getDescription());
        tripDTO.setCost(t.getCost());
        tripDTO.setMatch(t.getMatch());
        tripDTO.setImagePath(t.getImagePath());
        tripDTO.setUser(userToSeeDTO(t.getUser()));
        tripDTO.setCategories(t.getCategories());
        tripDTO.setComments(t.getComments());

        if (t.getImagePath() != null && !t.getImagePath().trim().isEmpty()) {
            try {
                tripDTO.setImage(ImageUtils.getImage(t.getImagePath()));
            } catch (IOException e) {
                tripDTO.setImage(null);
            }
        } else {
            tripDTO.setImage(null);
        }
        if ( t.getUser().getImagePath() != null && !t.getUser().getImagePath().trim().isEmpty()) {
            try {
                tripDTO.getUser().setImage(ImageUtils.getImage(t.getUser().getImagePath()));
            } catch (IOException e) {
                tripDTO.getUser().setImage(null);
            }
        } else {
            tripDTO.getUser().setImage(null);
        }


        return tripDTO;
    }

    default TripListDTO tripListToDto(Trip t) throws IOException {
        TripListDTO dto = new TripListDTO();

        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setImagePath(t.getImagePath());
        dto.setUser(userToSeeDTO(t.getUser()));

        if (t.getImagePath() != null && !t.getImagePath().trim().isEmpty()) {
            try {
                dto.setImage(ImageUtils.getImage(t.getImagePath()));
            } catch (IOException e) {
                dto.setImage(null);
            }
        } else {
            dto.setImage(null);
        }

        if ( t.getUser().getImagePath() != null && !t.getUser().getImagePath().trim().isEmpty()) {
            try {
                dto.getUser().setImage(ImageUtils.getImage(t.getImagePath()));
            } catch (IOException e) {
                dto.getUser().setImage(null);
            }
        } else {
            dto.getUser().setImage(null);
        }
        return dto;
    }
    }