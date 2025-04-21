package com.payment_service.dtos;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private Double amount;
}
