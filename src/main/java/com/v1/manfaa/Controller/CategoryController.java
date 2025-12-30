package com.v1.manfaa.Controller;


import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.CategoryDTOIn;
import com.v1.manfaa.DTO.Out.CategoryDTOOut;
import com.v1.manfaa.DTO.Out.SkillsDTOOut;
import com.v1.manfaa.Model.Category;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/get") // users and admins
     public ResponseEntity<?> getAllCategory() {
        return ResponseEntity.status(200).body(categoryService.getAllCategory());
    }

    @PostMapping("/add") // admin
    public ResponseEntity<?> addCategory(@RequestBody @Valid  CategoryDTOIn categoryDTOIn, @AuthenticationPrincipal User user) {
        categoryService.addCategory(user.getId(), categoryDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Category added"));
    }

    @PutMapping("/update/{categoryId}") // admin
    public ResponseEntity<?> updateCategory(@PathVariable Integer categoryId, @RequestBody @Valid CategoryDTOIn categoryDTOIn , @AuthenticationPrincipal User user) {
        categoryService.updateCategory(user.getId(), categoryId, categoryDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Category updated"));
    }

    @DeleteMapping("/delete/{categoryId}") // admin
    public ResponseEntity<?> deleteCategory(@PathVariable Integer categoryId , @AuthenticationPrincipal User user) {
        categoryService.deleteCategory(user.getId(), categoryId);
        return ResponseEntity.status(200).body(new ApiResponse("Category deleted"));
    }
}
