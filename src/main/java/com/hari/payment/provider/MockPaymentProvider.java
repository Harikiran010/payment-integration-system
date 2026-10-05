package com.hari.payment.provider;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.hari.payment.entity.PaymentStatus;

@Component
public class MockPaymentProvider {

    private final Random random = new Random();

    public PaymentStatus processPayment(BigDecimal amount) {

        // Simulate payment processing
        boolean successful = random.nextInt(100) < 90;

        if (successful) {
            return PaymentStatus.SUCCESS;
        }

        return PaymentStatus.FAILED;
    }
}