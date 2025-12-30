package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.UserDTOIn;
import com.v1.manfaa.DTO.Out.UserDTOOut;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.UserRepository;
import com.v1.manfaa.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private UserDTOIn userDTOIn;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");
        user1.setEmail("user1@test.com");
        user1.setFullName("User One");
        user1.setPhone_Number("1234567890");
        user1.setRole("ADMIN");
        user1.setPassword("hashedPassword1");

        user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setFullName("User Two");
        user2.setPhone_Number("0987654321");
        user2.setRole("ADMIN");
        user2.setPassword("hashedPassword2");

        userDTOIn = new UserDTOIn("newuser","newuser@test.com","New User","1112223333@adc1","password123");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTOOut> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UserDTOOut> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser_ShouldSaveUserWithHashedPassword() {
        // Arrange
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        userService.addUser(userDTOIn);

        // Assert
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@test.com", savedUser.getEmail());
        assertEquals("New User", savedUser.getFullName());
        assertEquals("1112223333@adc1", savedUser.getPhone_Number());
        assertEquals("ADMIN", savedUser.getRole());
        assertEquals("0000000000", savedUser.getRecordNumber());
        
        // Verify password is hashed (not plain text)
        assertNotEquals("password123", savedUser.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("password123", savedUser.getPassword()));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        when(userRepository.findUserById(user1.getId())).thenReturn(user1);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        userService.updateUser(user1.getId(), userDTOIn);

        // Assert
        verify(userRepository, times(1)).findUserById(user1.getId());
        verify(userRepository, times(1)).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals("newuser", updatedUser.getUsername());
        assertEquals("newuser@test.com", updatedUser.getEmail());
        assertEquals("New User", updatedUser.getFullName());
        assertEquals("1112223333@adc1", updatedUser.getPhone_Number());
        assertTrue(new BCryptPasswordEncoder().matches("password123", updatedUser.getPassword()));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> userService.updateUser(999, userDTOIn));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findUserById(999);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userRepository.findUserById(user1.getId())).thenReturn(user1);

        // Act
        userService.deleteUser(user1.getId());

        // Assert
        verify(userRepository, times(1)).findUserById(user1.getId());
        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> userService.deleteUser(999));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findUserById(999);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void convertToDtoOut_ShouldConvertUsersToDTO() {
        // Arrange
        List<User> users = Arrays.asList(user1, user2);

        // Act
        List<UserDTOOut> result = userService.convertToDtoOut(users);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        UserDTOOut dto1 = result.get(0);
        assertEquals(1, dto1.getId());
        assertEquals("user1", dto1.getUsername());
        assertEquals("user1@test.com", dto1.getEmail());
        assertEquals("User One", dto1.getFullName());
        assertEquals("1234567890", dto1.getPhone_Number());
        assertEquals("ADMIN", dto1.getRole());

        UserDTOOut dto2 = result.get(1);
        assertEquals(2, dto2.getId());
        assertEquals("user2", dto2.getUsername());
    }

    @Test
    void convertToDtoOut_ShouldReturnEmptyList_WhenGivenEmptyList() {
        // Act
        List<UserDTOOut> result = userService.convertToDtoOut(Arrays.asList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
