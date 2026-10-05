package com.hari.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentIntegrationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            PaymentIntegrationSystemApplication.class,
            args
        );
    }
}