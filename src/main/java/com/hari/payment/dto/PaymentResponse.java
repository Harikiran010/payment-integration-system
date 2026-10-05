package com.hari.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hari.payment.entity.PaymentStatus;

public class PaymentResponse {

    private Long id;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            String customerId,
            BigDecimal amount,
            String currency,
            PaymentStatus status, 
            LocalDateTime createdAt) {

        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}