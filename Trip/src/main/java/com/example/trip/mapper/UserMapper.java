package com.example.trip.mapper;

import com.example.trip.dto.SignInDTO;
import com.example.trip.model.Users;
import com.example.trip.service.ImageUtils;
import org.mapstruct.Mapper;

import java.io.IOException;

@Mapper(componentModel="spring")
public interface UserMapper {

    default SignInDTO toSignInDTO(Users user) {
        if (user == null) {
            return null;
        }
        SignInDTO dto = new SignInDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setImagePath(user.getImagePath());

        if (user.getImagePath() != null && !user.getImagePath().trim().isEmpty()) {
            try {
                dto.setImage(ImageUtils.getImage(user.getImagePath()));
            } catch (IOException e) {
                dto.setImage(null);
            }
        } else {
            dto.setImage(null);
        }
        return dto;
    }
}
