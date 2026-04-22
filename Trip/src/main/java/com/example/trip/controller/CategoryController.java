package com.example.trip.controller;

import com.example.trip.mapper.CategoryMapper;
import com.example.trip.model.Category;
import com.example.trip.model.Comment;
import com.example.trip.service.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/category")
@RestController
@CrossOrigin
public class CategoryController {
    private CategoryMapper categoryMapper;;
    private CategoryRepository categoryRepository;

    public CategoryController(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/getCategoryById/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category c = categoryRepository.findById(id).get();
            if (c == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(c, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories(){
        try {
            return new ResponseEntity<>(categoryRepository.findAll(),HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addCategory")
    public ResponseEntity<Category> addCategory(@RequestBody Category c){
        try {
            return new ResponseEntity<>(categoryRepository.save(c), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
