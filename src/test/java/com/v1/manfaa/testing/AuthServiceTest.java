package com.v1.manfaa.testing;

import com.v1.manfaa.DTO.In.LoginRequest;
import com.v1.manfaa.DTO.Out.LoginResponse;
import com.v1.manfaa.Service.AuthService;
import com.v1.manfaa.Service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        userDetails = new User("testuser", "password123", new ArrayList<>());
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
        // Arrange
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        // Act
        LoginResponse loginResponse = authService.login(loginRequest, response);

        // Assert
        assertNotNull(loginResponse);
        assertEquals(expectedToken, loginResponse.getToken());
        assertEquals("testuser", loginResponse.getUsername());
        assertEquals("Bearer", loginResponse.getRole());
        assertEquals("Login successful", loginResponse.getMessage());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername(loginRequest.getUsername());
        verify(jwtService, times(1)).generateToken(userDetails);
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void login_ShouldAddJwtCookie_WithCorrectAttributes() {
        // Arrange
        String expectedToken = "test-jwt-token";
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // Act
        authService.login(loginRequest, response);

        // Assert
        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        assertEquals("jwt", capturedCookie.getName());
        assertEquals(expectedToken, capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(24 * 60 * 60, capturedCookie.getMaxAge());
    }

    @Test
    void logout_ShouldDeleteJwtCookie() {
        // Arrange
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // Act
        authService.logout(response);

        // Assert
        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        assertEquals("jwt", capturedCookie.getName());
        assertNull(capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(0, capturedCookie.getMaxAge());
    }

    @Test
    void login_ShouldCallAuthenticationManager_WithCorrectCredentials() {
        // Arrange
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = 
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        // Act
        authService.login(loginRequest, response);

        // Assert
        verify(authenticationManager).authenticate(authCaptor.capture());
        UsernamePasswordAuthenticationToken capturedAuth = authCaptor.getValue();
        assertEquals("testuser", capturedAuth.getPrincipal());
        assertEquals("password123", capturedAuth.getCredentials());
    }
}
