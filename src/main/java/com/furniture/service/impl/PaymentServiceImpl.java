package com.furniture.service.impl;

import com.furniture.config.VNPayConfig;
import com.furniture.domain.OrderStatus;
import com.furniture.domain.PaymentMethod;
import com.furniture.domain.PaymentOrderStatus;
import com.furniture.domain.PaymentStatus;
import com.furniture.modal.Order;
import com.furniture.modal.PaymentOrder;
import com.furniture.modal.User;
import com.furniture.repository.OrderRepository;
import com.furniture.repository.PaymentOrderRepository;
import com.furniture.service.PaymentService;
import com.furniture.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final OrderRepository orderRepository;
    private final VNPayConfig vnPayConfig;

    @Override
    @Transactional
    public PaymentOrder createOrder(User user, Set<Order> orders) {
        Long amount = orders.stream()
                .mapToLong(order -> order.getTotalSellingPrice())
                .sum();

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(amount);
        paymentOrder.setUser(user);
        paymentOrder.setOrders(orders);
        paymentOrder.setPaymentMethod(PaymentMethod.VNPAY);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);

        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long orderId) throws Exception {
        return paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Payment order not found with id: " + orderId));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception {
        PaymentOrder paymentOrder = paymentOrderRepository.findById(Long.parseLong(paymentId))
                .orElseThrow(() -> new Exception("Payment order not found"));
        return paymentOrder;
    }

    @Override
    @Transactional
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder,
                                       String paymentId,
                                       String paymentLinkId) throws Exception {
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentOrder.setPaymentLinkId(paymentLinkId);
            paymentOrderRepository.save(paymentOrder);

            // Update orders status
            for (Order order : paymentOrder.getOrders()) {
                order.setPaymentStatus(PaymentStatus.COMPLETED);
                order.setOrderStatus(OrderStatus.PLACED);
                orderRepository.save(order);
            }
            return true;
        }
        return false;
    }

    @Override
    public String createVnpayPaymentUrl(PaymentOrder paymentOrder,
                                        HttpServletRequest request) throws Exception {

        String vnp_Version = vnPayConfig.getVersion();
        String vnp_Command = vnPayConfig.getCommand();
        String orderType = vnPayConfig.getOrderType();

        long amount = paymentOrder.getAmount() * 100; // VNPay yêu cầu nhân 100
        String bankCode = "";

        String vnp_TxnRef = VNPayUtil.getRandomNumber(8);
        String vnp_IpAddr = VNPayUtil.getIpAddress(request);
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + paymentOrder.getId());
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getVnpUrl() + "?" + queryUrl;

        // Save payment link id
        paymentOrder.setPaymentLinkId(vnp_TxnRef);
        paymentOrderRepository.save(paymentOrder);

        return paymentUrl;
    }

    @Override
    @Transactional
    public Map<String, String> vnpayReturn(Map<String, String> params) throws Exception {
        Map<String, String> result = new HashMap<>();

        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        params.remove("vnp_SecureHash");

        // Verify checksum
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String signValue = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());

        if (signValue.equals(vnp_SecureHash)) {
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");

            if ("00".equals(vnp_ResponseCode)) {
                // Payment success
                PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(vnp_TxnRef);
                if (paymentOrder != null) {
                    proceedPaymentOrder(paymentOrder, vnp_TransactionNo, vnp_TxnRef);
                    result.put("status", "success");
                    result.put("message", "Thanh toán thành công");
                    result.put("orderId", paymentOrder.getId().toString());
                } else {
                    result.put("status", "error");
                    result.put("message", "Không tìm thấy đơn hàng");
                }
            } else {
                // Payment failed
                result.put("status", "failed");
                result.put("message", "Thanh toán thất bại. Mã lỗi: " + vnp_ResponseCode);
            }
        } else {
            result.put("status", "error");
            result.put("message", "Chữ ký không hợp lệ");
        }

        return result;
    }
}