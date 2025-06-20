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

    @PostMapping("/create-order/{bookingId}")
    public ResponseEntity<?> createPaymentOrder(@PathVariable Long bookingId) {
        try {
            Order order = paymentService.createPaymentOrder(bookingId);
            return ResponseEntity.ok(order.toString());
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order", e);
            return ResponseEntity.status(500).body("Error creating payment order");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        Map<String, String> result = paymentService.verifyPayment(payload);
        if ("success".equals(result.get("status"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(400).body(result);
        }
    }
}