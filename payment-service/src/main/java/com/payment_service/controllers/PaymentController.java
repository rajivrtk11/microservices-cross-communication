package com.payment_service.controllers;

import com.payment_service.dtos.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping("/pay")
    public ResponseEntity<String> makePayment(@RequestBody PaymentRequest paymentRequest) {
        // Mock logic: you can integrate a real payment gateway here
        System.out.println("Processing payment for Order ID: " + paymentRequest.getOrderId() +
                " with amount: " + paymentRequest.getAmount());

        return ResponseEntity.ok("Payment successful for Order ID: " + paymentRequest.getOrderId());
    }
}

