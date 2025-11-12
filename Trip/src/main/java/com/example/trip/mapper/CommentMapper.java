package com.example.trip.mapper;

import com.example.trip.dto.CommentDTO;
import com.example.trip.dto.UserToSeeDTO;
import com.example.trip.model.Comment;
import com.example.trip.model.Users;

import java.io.IOException;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface CommentMapper {
    UserToSeeDTO userToSeeDTO(Users user);

    List<CommentDTO> commentsListDTO(List<Comment> comments);

    default CommentDTO commentToDto(Comment c) throws IOException {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId(c.getId());
        commentDTO.setComment(c.getComment());
        commentDTO.setDate(c.getDate());
        commentDTO.setUser(userToSeeDTO(c.getUser()));
        return commentDTO;
    }
}
