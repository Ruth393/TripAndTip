package com.example.trip.mapper;

import com.example.trip.dto.CommentDTO;
import com.example.trip.dto.CommentToAddDTO;
import com.example.trip.dto.UserToSeeDTO;
import com.example.trip.model.Comment;
import com.example.trip.model.Trip;
import com.example.trip.model.Users;
import com.example.trip.service.ImageUtils;
import org.mapstruct.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    UserToSeeDTO userToSeeDTO(Users user);
    List<CommentDTO> commentsListDTO(List<Comment> comments);

    default Comment toComment(CommentToAddDTO dto) {
        Comment comment = new Comment();
        comment.setComment(dto.getComment());
        comment.setDate(dto.getDate());
        comment.setTrip(dto.getTrip());
        return comment;
    }

    default CommentDTO commentToDto(Comment c) throws IOException {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId(c.getId());
        commentDTO.setComment(c.getComment());
        commentDTO.setDate(c.getDate());
        commentDTO.setUser(userToSeeDTO(c.getUser()));

        if (c.getUser() != null && c.getUser().getImagePath() != null && !c.getUser().getImagePath().trim().isEmpty()) {
            try {
                commentDTO.getUser().setImage(ImageUtils.getImage(c.getUser().getImagePath()));
            } catch (IOException e) {
                commentDTO.getUser().setImage(null);
            }
        } else {
            commentDTO.getUser().setImage(null);
        }
        return commentDTO;
    }
}