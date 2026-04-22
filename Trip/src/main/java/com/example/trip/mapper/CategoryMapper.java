package com.example.trip.mapper;

import com.example.trip.model.Category;
import org.mapstruct.Mapper;

import java.io.IOException;
import java.util.List;
@Mapper(componentModel="spring")
public interface CategoryMapper {

        List<Category> categoryList(List<Category> categories);

        default Category category(Category c) throws IOException {
            Category category = new Category();

            category.setId(c.getId());
            category.setCategory(c.getCategory());
            return category;
        }
    }
