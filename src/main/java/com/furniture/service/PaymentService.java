package com.furniture.service;

import com.furniture.modal.Order;
import com.furniture.modal.PaymentOrder;
import com.furniture.modal.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Set;

public interface PaymentService {
    PaymentOrder createOrder(User user, Set<Order> orders);

    PaymentOrder getPaymentOrderById(Long orderId) throws Exception;

    PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder,
                                String paymentId,
                                String paymentLinkId) throws Exception;

    String createVnpayPaymentUrl(PaymentOrder paymentOrder, HttpServletRequest request) throws Exception;

    Map<String, String> vnpayReturn(Map<String, String> params) throws Exception;
}