package com.hari.payment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hari.payment.dto.PaymentRequest;
import com.hari.payment.dto.PaymentResponse;
import com.hari.payment.service.PaymentService;

import jakarta.validation.Valid;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping("/payments/process")
    public PaymentResponse processPayment(
            @Valid @RequestBody PaymentRequest request) {

        return paymentService.processPayment(request);
    }
}