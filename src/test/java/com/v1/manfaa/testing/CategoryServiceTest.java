package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.CategoryDTOIn;
import com.v1.manfaa.DTO.Out.CategoryDTOOut;
import com.v1.manfaa.Model.Category;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.CategoryRepository;
import com.v1.manfaa.Repository.UserRepository;
import com.v1.manfaa.Service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User admin;
    private Category category1;
    private Category category2;
    private CategoryDTOIn categoryDTOIn;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setRole("ADMIN");

        category1 = new Category(1, "Technology", "Tech services", null, null);
        category2 = new Category(2, "Marketing", "Marketing services", null, null);

        categoryDTOIn = new CategoryDTOIn("Design","Design services");
    }

    @Test
    void getAllCategory_ShouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<CategoryDTOOut> result = categoryService.getAllCategory();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).getName());
        assertEquals("Marketing", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void addCategory_ShouldSaveCategory_WhenUserExists() {
        // Arrange
        when(userRepository.findUserById(admin.getId())).thenReturn(admin);

        // Act
        categoryService.addCategory(admin.getId(), categoryDTOIn);

        // Assert
        verify(userRepository, times(1)).findUserById(admin.getId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void addCategory_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, 
                () -> categoryService.addCategory(999, categoryDTOIn));
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findUserById(999);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateCategory_WhenCategoryAndUserExist() {
        // Arrange
        when(userRepository.findUserById(admin.getId())).thenReturn(admin);
        when(categoryRepository.findCategoryById(category1.getId())).thenReturn(category1);

        // Act
        categoryService.updateCategory(admin.getId(), category1.getId(), categoryDTOIn);

        // Assert
        verify(userRepository, times(1)).findUserById(admin.getId());
        verify(categoryRepository, times(1)).findCategoryById(category1.getId());
        verify(categoryRepository, times(1)).save(category1);
        assertEquals("Design", category1.getName());
        assertEquals("Design services", category1.getDescription());
    }

    @Test
    void updateCategory_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> categoryService.updateCategory(999, category1.getId(), categoryDTOIn));

        assertEquals("User not found", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(userRepository.findUserById(admin.getId())).thenReturn(admin);
        when(categoryRepository.findCategoryById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> categoryService.updateCategory(admin.getId(), 999, categoryDTOIn));

        assertEquals("category not found ", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory_WhenCategoryAndUserExist() {
        // Arrange
        when(userRepository.findUserById(admin.getId())).thenReturn(admin);
        when(categoryRepository.findCategoryById(category1.getId())).thenReturn(category1);

        // Act
        categoryService.deleteCategory(admin.getId(), category1.getId());

        // Assert
        verify(userRepository, times(1)).findUserById(admin.getId());
        verify(categoryRepository, times(1)).findCategoryById(category1.getId());
        verify(categoryRepository, times(1)).delete(category1);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> categoryService.deleteCategory(999, category1.getId()));

        assertEquals("User not found", exception.getMessage());
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(userRepository.findUserById(admin.getId())).thenReturn(admin);
        when(categoryRepository.findCategoryById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> categoryService.deleteCategory(admin.getId(), 999));

        assertEquals("category not found ", exception.getMessage());
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void convertToDtoOut_ShouldConvertCategoriesToDTOs() {
        // Arrange
        List<Category> categories = Arrays.asList(category1, category2);

        // Act
        List<CategoryDTOOut> result = categoryService.convertToDtoOut(categories);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).getName());
        assertEquals("Tech services", result.get(0).getDescription());
        assertEquals("Marketing", result.get(1).getName());
        assertEquals("Marketing services", result.get(1).getDescription());
    }

    @Test
    void convertToDtoOut_ShouldReturnEmptyList_WhenGivenEmptyList() {
        // Arrange
        List<Category> categories = Arrays.asList();

        // Act
        List<CategoryDTOOut> result = categoryService.convertToDtoOut(categories);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
