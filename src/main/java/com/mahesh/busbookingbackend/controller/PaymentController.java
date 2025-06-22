package com.mahesh.busbookingbackend.controller;

import com.mahesh.busbookingbackend.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate-demo-payment/{bookingId}")
    public ResponseEntity<?> createDemoPaymentOrder(@PathVariable Long bookingId) {
        Map<String, String> result = paymentService.createDemoPayment(bookingId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm-demo-payment/{bookingId}")
    public ResponseEntity<?> confirmDemoPayment(@PathVariable Long bookingId) {
        Map<String, String> result = paymentService.confirmDemoPayment(bookingId);
        return ResponseEntity.ok(result);
    }
}