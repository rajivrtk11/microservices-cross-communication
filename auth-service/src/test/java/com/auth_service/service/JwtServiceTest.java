package com.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtService.generateToken("testUser");
        assertNotNull(token);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken("testUser");
        String username = jwtService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void extractExpiration_shouldReturnFutureDate() {
        String token = jwtService.generateToken("testUser");
        Date expiration = jwtService.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("testUser");
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testUser");

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidUsername() {
        String token = jwtService.generateToken("testUser");
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("wrongUser");

        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void getClaims_shouldReturnClaimsForValidToken() {
        String token = jwtService.generateToken("testUser");
        Claims claims = jwtService.getClaims(token);

        assertNotNull(claims);
        assertEquals("testUser", claims.getSubject());
    }

    @Test
    void getClaims_shouldReturnNullForExpiredToken() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService() {
            @Override
            public String generateToken(String userName) {
                return Jwts.builder()
                        .setSubject(userName)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 100)) // 100ms expiry
                        .signWith(getSignKey())
                        .compact();
            }
        };

        String token = shortLivedJwtService.generateToken("expiredUser");
        Thread.sleep(200); // wait for token to expire

        assertNull(shortLivedJwtService.getClaims(token));
    }
}
