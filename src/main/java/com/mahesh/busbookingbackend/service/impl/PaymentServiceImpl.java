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

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final BusBookingRepository busBookingRepository;
    private final PaymentRepository paymentRepository;
    private final BusBookingService busBookingService;

    @Value("${razorpay.key.secret}")
    private String secretKey;

    @Override
    public Order createPaymentOrder(Long bookingId) throws RazorpayException {
        BusBookingEntity booking = busBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        int amountInPaisa = (int) (booking.getTotalPrice() * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaisa);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "booking_rcpt_" + bookingId);

        Order order = razorpayClient.orders.create(orderRequest);
        log.info("Razorpay Order created: {}", order);
        return order;
    }

    @Override
    @Transactional
    public Map<String, String> verifyPayment(Map<String, String> payload) {
        String razorpayOrderId = payload.get("razorpay_order_id");
        String razorpayPaymentId = payload.get("razorpay_payment_id");
        String razorpaySignature = payload.get("razorpay_signature");
        String receipt = payload.get("receipt");
        Long bookingId = Long.parseLong(receipt.split("_")[2]);

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean signatureVerified = Utils.verifyPaymentSignature(options, secretKey);

            if (signatureVerified) {
                log.info("Payment signature verified successfully for order_id: {}", razorpayOrderId);

                BusBookingEntity booking = busBookingRepository.findById(bookingId)
                        .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
                PaymentEntity payment = new PaymentEntity();
                payment.setRazorpayOrderId(razorpayOrderId);
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setBusBooking(booking);

                paymentRepository.save(payment);
                booking.setPayment(payment);
                busBookingRepository.save(booking);
                busBookingService.confirmPayment(bookingId);

                return Map.of("status", "success", "message", "Payment verified and booking confirmed.");
            } else {
                log.warn("Payment signature verification failed for order_id: {}", razorpayOrderId);
                return Map.of("status", "failure", "message", "Payment verification failed.");
            }
        } catch (Exception e) {
            log.error("Error during payment verification for order_id: {}", razorpayOrderId, e);
            return Map.of("status", "error", "message", "Internal server error during payment verification.");
        }
    }
}