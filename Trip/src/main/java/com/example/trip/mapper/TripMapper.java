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
        tripDTO.setImage(t.getImage());
        tripDTO.setImagePath(t.getImagePath());
        tripDTO.setUser(userToSeeDTO(t.getUser()));
        tripDTO.setCategories(t.getCategories());
        tripDTO.setComments(t.getComments());

        tripDTO.setImage(ImageUtils.getImage(t.getImagePath()));


        return tripDTO;
    }

    default TripListDTO tripListToDto(Trip t) throws IOException {
        TripListDTO tripListDTO = new TripListDTO();

        tripListDTO.setId(t.getId());
        tripListDTO.setName(t.getName());
        tripListDTO.setImage(t.getImage());
        tripListDTO.setImagePath(t.getImagePath());
        tripListDTO.setUser(userToSeeDTO(t.getUser()));

        tripListDTO.setImage(ImageUtils.getImage(t.getImagePath())); // עבר לשימוש ב-getImagePath

        return tripListDTO;
    }
}