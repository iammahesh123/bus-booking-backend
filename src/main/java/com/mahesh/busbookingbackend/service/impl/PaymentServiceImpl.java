package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.entity.BusBookingEntity;
import com.mahesh.busbookingbackend.entity.PaymentEntity;
import com.mahesh.busbookingbackend.enums.PaymentStatus;
import com.mahesh.busbookingbackend.exception.ResourceNotFoundException;
import com.mahesh.busbookingbackend.repository.BusBookingRepository;
import com.mahesh.busbookingbackend.repository.PaymentRepository;
import com.mahesh.busbookingbackend.service.BusBookingService;
import com.mahesh.busbookingbackend.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final BusBookingRepository busBookingRepository;
    private final PaymentRepository paymentRepository;
    private final BusBookingService busBookingService;

//    @Value("${razorpay.key.secret}")
//    private String secretKey;

    @Override
    public Map<String, String> createDemoPayment(Long bookingId) {
        // In a real scenario, this would create a payment intent with a provider.
        // For demo purposes, we just confirm the booking can proceed to payment.
        busBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        log.info("Demo payment initiated for bookingId: {}", bookingId);
        return Map.of(
                "status", "success",
                "message", "Payment initiated. Please confirm to complete.",
                "bookingId", bookingId.toString()
        );
    }

    @Override
    @Transactional
    public Map<String, String> confirmDemoPayment(Long bookingId) {
        log.info("Confirming payment for bookingId: {}", bookingId);

        BusBookingEntity booking = busBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Create a dummy payment record
        PaymentEntity payment = new PaymentEntity();
        payment.setRazorpayOrderId("DEMO_ORDER_" + UUID.randomUUID().toString());
        payment.setRazorpayPaymentId("DEMO_PAYMENT_" + UUID.randomUUID().toString());
        payment.setRazorpaySignature("DEMO_SIGNATURE");
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setBusBooking(booking);

        // Save the new PaymentEntity to the database
        paymentRepository.save(payment);

        booking.setPayment(payment);
        busBookingRepository.save(booking);

        // Confirm the booking
        busBookingService.confirmPayment(bookingId);

        log.info("Payment confirmed and booking completed for bookingId: {}", bookingId);
        return Map.of("status", "success", "message", "Payment verified and booking confirmed.");
    }
}