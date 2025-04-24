package com.auth_service.service;

import com.auth_service.dto.Product;
import com.auth_service.entity.UserInfo;
import com.auth_service.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService.loadProductsFromDB(); // manually trigger @PostConstruct
    }

    @Test
    void getProducts_shouldReturnListOf100Products() {
        List<Product> products = productService.getProducts();
        assertNotNull(products);
        assertEquals(100, products.size());
    }

    @Test
    void getProduct_validId_shouldReturnCorrectProduct() {
        Product product = productService.getProduct(50);
        assertNotNull(product);
        assertEquals(50, product.getProductId());
        assertTrue(product.getName().contains("product"));
    }

    @Test
    void getProduct_invalidId_shouldThrowException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.getProduct(200));
        assertEquals("product 200 not found", ex.getMessage());
    }

    @Test
    void addUser_shouldEncodePasswordAndSaveUser() {
        UserInfo user = new UserInfo();
        user.setName("john");
        user.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        String result = productService.addUser(user);

        assertEquals("user added to system ", result);
        assertEquals("encodedPassword", user.getPassword());
        verify(userInfoRepository, times(1)).save(user);
    }
}
