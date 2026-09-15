package com.example.trip.mapper;

import com.example.trip.dto.CommentDTO;
import com.example.trip.dto.CommentToAddDTO;
import com.example.trip.dto.ImageDTO;
import com.example.trip.dto.UserToSeeDTO;
import com.example.trip.model.Comment;
import com.example.trip.model.CommentImage;
import com.example.trip.model.Users;
import com.example.trip.service.ImageUtils;
import org.mapstruct.*;

import java.io.IOException;
import java.util.ArrayList;
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

    // ממיר את רשימת ה-CommentImage של התגובה לרשימת ImageDTO (עם Base64)
    default List<ImageDTO> commentImagesToDto(List<CommentImage> images) {
        List<ImageDTO> result = new ArrayList<>();
        if (images == null) return result;
        for (CommentImage ci : images) {
            try {
                result.add(new ImageDTO(ci.getId(), ImageUtils.getImage(ci.getImagePath())));
            } catch (IOException e) {
                // מדלגים על תמונה שלא נמצאה, לא מפילים את כל התגובה
            }
        }
        return result;
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
            if (commentDTO.getUser() != null) {
                commentDTO.getUser().setImage(null);
            }
        }

        // תמונות התגובה (חדש)
        commentDTO.setImages(commentImagesToDto(c.getImages()));

        return commentDTO;
    }
}