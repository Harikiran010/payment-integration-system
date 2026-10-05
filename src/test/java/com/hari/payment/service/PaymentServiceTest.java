package com.hari.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hari.payment.dto.PaymentRequest;
import com.hari.payment.dto.PaymentResponse;
import com.hari.payment.entity.Payment;
import com.hari.payment.entity.PaymentStatus;
import com.hari.payment.exception.PaymentException;
import com.hari.payment.provider.MockPaymentProvider;
import com.hari.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MockPaymentProvider paymentProvider;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        request = new PaymentRequest();

        request.setCustomerId("CUST200");
        request.setAmount(new BigDecimal("1000"));
        request.setCurrency("INR");
        request.setIdempotencyKey("PAY-20001");
    }

    @Test
    void shouldProcessNewPayment() {

        when(paymentRepository.findByIdempotencyKey("PAY-20001"))
                .thenReturn(Optional.empty());

        when(paymentProvider.processPayment(request.getAmount()))
                .thenReturn(PaymentStatus.SUCCESS);

        Payment savedPayment = new Payment();

        savedPayment.setIdempotencyKey("PAY-20001");
        savedPayment.setCustomerId("CUST200");
        savedPayment.setAmount(new BigDecimal("1000"));
        savedPayment.setCurrency("INR");
        savedPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        PaymentResponse response =
                paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("CUST200", response.getCustomerId());
        assertEquals(new BigDecimal("1000"), response.getAmount());
        assertEquals("INR", response.getCurrency());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentProvider).processPayment(request.getAmount());
    }

    @Test
    void shouldReturnExistingPaymentForSameIdempotencyKey() {

        Payment existingPayment = new Payment();

        existingPayment.setIdempotencyKey("PAY-20001");
        existingPayment.setCustomerId("CUST200");
        existingPayment.setAmount(new BigDecimal("1000"));
        existingPayment.setCurrency("INR");
        existingPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByIdempotencyKey("PAY-20001"))
                .thenReturn(Optional.of(existingPayment));

        PaymentResponse response =
                paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("CUST200", response.getCustomerId());
        assertEquals(new BigDecimal("1000"), response.getAmount());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentProvider, never()).processPayment(any());
    }

    @Test
    void shouldThrowExceptionWhenIdempotencyKeyUsedWithDifferentDetails() {

        Payment existingPayment = new Payment();

        existingPayment.setIdempotencyKey("PAY-20001");
        existingPayment.setCustomerId("CUST200");
        existingPayment.setAmount(new BigDecimal("1000"));
        existingPayment.setCurrency("INR");
        existingPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByIdempotencyKey("PAY-20001"))
                .thenReturn(Optional.of(existingPayment));

        request.setAmount(new BigDecimal("2000"));

        assertThrows(
                PaymentException.class,
                () -> paymentService.processPayment(request)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentProvider, never()).processPayment(any());
    }

    @Test
    void shouldGetAllPayments() {

        Payment payment1 = new Payment();

        payment1.setCustomerId("CUST101");
        payment1.setAmount(new BigDecimal("500"));
        payment1.setCurrency("INR");
        payment1.setStatus(PaymentStatus.SUCCESS);

        Payment payment2 = new Payment();

        payment2.setCustomerId("CUST102");
        payment2.setAmount(new BigDecimal("1000"));
        payment2.setCurrency("INR");
        payment2.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findAll())
                .thenReturn(List.of(payment1, payment2));

        List<PaymentResponse> responses =
                paymentService.getAllPayments();

        assertEquals(2, responses.size());

        assertEquals("CUST101", responses.get(0).getCustomerId());
        assertEquals("CUST102", responses.get(1).getCustomerId());

        verify(paymentRepository).findAll();
    }
}