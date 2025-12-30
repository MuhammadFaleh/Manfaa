package com.v1.manfaa.testing;

import com.v1.manfaa.Service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private String secretKey;
    private long jwtExpiration;

    @BeforeEach
    void setUp() {
        // Generate a valid 256-bit (32 bytes) secret key for HS256
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            keyBytes[i] = (byte) i;
        }
        secretKey = Base64.getEncoder().encodeToString(keyBytes);
        jwtExpiration = 86400000L; // 24 hours

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);

        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateToken_WithExtraClaims_ShouldGenerateValidToken() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("email", "test@example.com");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValidAndUserMatches() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User("differentuser", "password", new ArrayList<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractClaim_ShouldExtractSubject() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        // Assert
        assertEquals("testuser", subject);
    }

    @Test
    void extractClaim_ShouldExtractExpiration() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

//    @Test
//    void generateToken_ShouldSetIssuedAtTime() {
//        // Arrange
//        long beforeGeneration = System.currentTimeMillis();
//
//        // Act
//        String token = jwtService.generateToken(userDetails);
//
//        // Assert
//        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
//        assertNotNull(issuedAt);
//        assertTrue(issuedAt.getTime()>= beforeGeneration);
//        assertTrue(issuedAt.getTime() <= System.currentTimeMillis());
//    }

    @Test
    void generateToken_ShouldSetCorrectExpiration() {
        // Arrange
        long beforeGeneration = System.currentTimeMillis();
        
        // Act
        String token = jwtService.generateToken(userDetails);
        
        // Assert
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);
        
        long expectedExpiration = beforeGeneration + jwtExpiration;
        long actualExpiration = expiration.getTime();
        
        // Allow 1 second tolerance
        assertTrue(Math.abs(actualExpiration - expectedExpiration) < 1000);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        // Arrange - Set very short expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1 millisecond
        String token = jwtService.generateToken(userDetails);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail("Thread interrupted");
        }

        // Reset normal expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldContainClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        assertEquals("customValue", claims.get("customClaim"));
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void extractUsername_ShouldWorkForMultipleTokens() {
        // Arrange
        UserDetails user1 = new User("user1", "password", new ArrayList<>());
        UserDetails user2 = new User("user2", "password", new ArrayList<>());
        
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        // Act
        String username1 = jwtService.extractUsername(token1);
        String username2 = jwtService.extractUsername(token2);

        // Assert
        assertEquals("user1", username1);
        assertEquals("user2", username2);
    }
}
