package com.v1.manfaa.Controller;


import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.CategoryDTOIn;
import com.v1.manfaa.DTO.Out.CategoryDTOOut;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Model.Category;
import com.v1.manfaa.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/get")
     public ResponseEntity<List<CategoryDTOOut>> getAllCategory() {
        return ResponseEntity.status(200).body(categoryService.getAllCategory());
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addCategory(@PathVariable Integer userId, @RequestBody @Valid  CategoryDTOIn categoryDTOIn) {
        categoryService.addCategory(userId, categoryDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Category added"));
    }

    @PutMapping("/update/{userId}/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer userId, @PathVariable Integer categoryId, @RequestBody @Valid CategoryDTOIn categoryDTOIn) {
        categoryService.updateCategory(userId, categoryId, categoryDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Category updated"));
    }

    @DeleteMapping("/delete/{userId}/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer userId, @PathVariable Integer categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.status(200).body(new ApiResponse("Category deleted"));
    }
}
