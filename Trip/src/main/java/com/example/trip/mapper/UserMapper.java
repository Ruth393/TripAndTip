package com.example.trip.mapper;

import com.example.trip.dto.SignInDTO;
import com.example.trip.dto.SignUpDTO;
import com.example.trip.model.Users;
import org.mapstruct.Mapper;
@Mapper(componentModel="spring")
public interface UserMapper {


    default SignInDTO toSignInDTO(Users user) {
        if (user == null) {
            return null;
        }

        SignInDTO dto = new SignInDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    default Users toSignUpDTO(SignUpDTO signUpDTO) {
        if (signUpDTO == null) {
            return null;
        }
        Users user = new Users();
        user.setName(signUpDTO.getName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(signUpDTO.getPassword());

        return user;
    }

}
