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
        dto.setUserName(user.getUserName());
        dto.setPassword(user.getPassword());
        return dto;
    }

    default Users toSignUpDTO(SignUpDTO signUpDTO) {
        if (signUpDTO == null) {
            return null;
        }
        Users user = new Users();
        user.setUserName(signUpDTO.getUserName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(signUpDTO.getPassword());

        return user;
    }

}
