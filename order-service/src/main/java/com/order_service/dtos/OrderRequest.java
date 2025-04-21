package com.order_service.dtos;

import lombok.Data;

@Data
public class OrderRequest {
    private String orderId;
    private Double amount;
    // Getters and setters
}

