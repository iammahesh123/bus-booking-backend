package com.mahesh.busbookingbackend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import java.util.Map;

public interface PaymentService {

    Order createPaymentOrder(Long bookingId) throws RazorpayException;
    Map<String, String> verifyPayment(Map<String, String> payload);
}