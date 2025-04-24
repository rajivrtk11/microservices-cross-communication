package com.auth_service.controller;

import com.auth_service.dto.Product;
import com.auth_service.exceptions.GlobalExceptionHandler;
import com.auth_service.service.JwtService;
import com.auth_service.service.ProductService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private ProductController productController;

    private Product sampleProduct;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        sampleProduct = new Product(1, "Laptop", 2, 999.99);
    }

    @Test
    void welcome_shouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/products/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome")));
    }

    @Test
    void addNewUser_shouldReturnSuccessMessage() throws Exception {
        when(productService.addUser(any())).thenReturn("User Added");

        mockMvc.perform(post("/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Added"));
    }

    @Test
    void getAllProducts_asAdmin_shouldReturnList() throws Exception {
        when(productService.getProducts()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/products/all")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").authorities(() -> "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getProductById_asUser_shouldReturnProduct() throws Exception {
        when(productService.getProduct(1)).thenReturn(sampleProduct);

        mockMvc.perform(get("/products/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").authorities(() -> "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void authenticateAndGetToken_shouldReturnJwtToken() throws Exception {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken("john")).thenReturn("fake-jwt");

        mockMvc.perform(post("/products/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt"));
    }

    @Test
    void authenticateAndGetToken_shouldThrowUsernameNotFoundException_whenAuthenticationFails() throws Exception {
        // Mocking authentication to return unauthenticated
        Authentication mockAuth = Mockito.mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        // Performing the request and asserting the result
        mockMvc.perform(post("/products/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized()) // Expected from the exception handler
                .andExpect(content().string("invalid user request !"));
    }


    @Test
    void validateToken_withValidToken_shouldReturnOK() throws Exception {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user123");
        when(jwtService.getClaims("valid-token")).thenReturn(claims);

        mockMvc.perform(get("/products/validate")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-User-Id", "user123"));
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnUnauthorized() throws Exception {
        when(jwtService.getClaims("invalid-token")).thenThrow(new RuntimeException("Invalid"));

        mockMvc.perform(get("/products/validate")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validateToken_withMissingBearerPrefix_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/products/validate")
                        .header("Authorization", "invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
