package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.UserRepository;
import com.v1.manfaa.Service.NewUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NewUserDetailsService newUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("hashedPassword123");
        user.setEmail("test@example.com");
        user.setRole("COMPANY");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);

        // Act
        UserDetails userDetails = newUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("hashedPassword123", userDetails.getPassword());
        verify(userRepository, times(1)).findUserByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findUserByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> newUserDetailsService.loadUserByUsername("nonexistent"));

        assertEquals("username or password is wrong", exception.getMessage());
        verify(userRepository, times(1)).findUserByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserWithAuthorities() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);

        // Act
        UserDetails userDetails = newUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertNotNull(userDetails.getAuthorities());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void loadUserByUsername_ShouldHandleDifferentUsernames() {
        // Arrange
        User user2 = new User();
        user2.setUsername("anotheruser");
        user2.setPassword("password456");

        when(userRepository.findUserByUsername("testuser")).thenReturn(user);
        when(userRepository.findUserByUsername("anotheruser")).thenReturn(user2);

        // Act
        UserDetails userDetails1 = newUserDetailsService.loadUserByUsername("testuser");
        UserDetails userDetails2 = newUserDetailsService.loadUserByUsername("anotheruser");

        // Assert
        assertEquals("testuser", userDetails1.getUsername());
        assertEquals("anotheruser", userDetails2.getUsername());
        verify(userRepository, times(1)).findUserByUsername("testuser");
        verify(userRepository, times(1)).findUserByUsername("anotheruser");
    }

    @Test
    void loadUserByUsername_ShouldReturnSameUserForMultipleCalls() {
        // Arrange
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);

        // Act
        UserDetails userDetails1 = newUserDetailsService.loadUserByUsername("testuser");
        UserDetails userDetails2 = newUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals(userDetails1.getUsername(), userDetails2.getUsername());
        verify(userRepository, times(2)).findUserByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldHandleEmptyUsername() {
        // Arrange
        when(userRepository.findUserByUsername("")).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> newUserDetailsService.loadUserByUsername(""));

        assertEquals("username or password is wrong", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldHandleUsernameWithSpecialCharacters() {
        // Arrange
        User specialUser = new User();
        specialUser.setUsername("user@123");
        specialUser.setPassword("password");
        
        when(userRepository.findUserByUsername("user@123")).thenReturn(specialUser);

        // Act
        UserDetails userDetails = newUserDetailsService.loadUserByUsername("user@123");

        // Assert
        assertNotNull(userDetails);
        assertEquals("user@123", userDetails.getUsername());
    }
}
