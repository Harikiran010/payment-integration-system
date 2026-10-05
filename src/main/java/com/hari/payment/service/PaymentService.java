package com.hari.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hari.payment.dto.PaymentRequest;
import com.hari.payment.dto.PaymentResponse;
import com.hari.payment.entity.Payment;
import com.hari.payment.entity.PaymentStatus;
import com.hari.payment.exception.PaymentException;
import com.hari.payment.provider.MockPaymentProvider;
import com.hari.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MockPaymentProvider paymentProvider;

    public PaymentService(
            PaymentRepository paymentRepository,
            MockPaymentProvider paymentProvider) {

        this.paymentRepository = paymentRepository;
        this.paymentProvider = paymentProvider;
    }

    public PaymentResponse processPayment(PaymentRequest request) {

        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                );

        if (existingPayment.isPresent()) {

            Payment existing = existingPayment.get();

            boolean sameCustomer =
                    existing.getCustomerId()
                            .equals(request.getCustomerId());

            boolean sameAmount =
                    existing.getAmount()
                            .compareTo(request.getAmount()) == 0;

            boolean sameCurrency =
                    existing.getCurrency()
                            .equalsIgnoreCase(request.getCurrency());

            if (!sameCustomer || !sameAmount || !sameCurrency) {

                throw new PaymentException(
                        "Idempotency key already used with different payment details"
                );
            }

            return convertToResponse(existing);
        }

        Payment payment = new Payment();

        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCustomerId(request.getCustomerId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());

        payment.setStatus(PaymentStatus.PENDING);

        PaymentStatus result =
                paymentProvider.processPayment(request.getAmount());

        payment.setStatus(result);

        Payment savedPayment =
                paymentRepository.save(payment);

        return convertToResponse(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private PaymentResponse convertToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}