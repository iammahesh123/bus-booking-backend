package com.mahesh.busbookingbackend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import java.util.Map;

public interface PaymentService {
    Map<String, String> createDemoPayment(Long bookingId);
    Map<String, String> confirmDemoPayment(Long bookingId);
}