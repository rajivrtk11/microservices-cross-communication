package com.order_service.controllers;


import com.order_service.dtos.OrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Value("${PAYMENT_SERVICE_URL}")
    private String paymentServiceUrl;

    private final RestTemplate restTemplate;

    public OrderController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        // Make a call to Payment service
        String paymentResponse = restTemplate.postForObject(
                paymentServiceUrl + "/payment/pay",
                orderRequest,
                String.class
        );

        return ResponseEntity.ok("Order placed. Payment Status: " + paymentResponse);
    }
}

