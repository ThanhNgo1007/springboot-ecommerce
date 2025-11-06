package com.furniture.controller;

import com.furniture.modal.*;
import com.furniture.response.ApiResponse;
import com.furniture.response.PaymentLinkResponse;
import com.furniture.service.PaymentService;
import com.furniture.service.SellerReportService;
import com.furniture.service.SellerService;
import com.furniture.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> paymentSuccessHandler(
            @PathVariable String paymentId,
            @RequestParam String paymentLinkId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        PaymentLinkResponse paymentLinkResponse;

        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentId);

        Boolean success = paymentService.proceedPaymentOrder(
                paymentOrder,
                paymentId,
                paymentLinkId
        );

        if(success){
            for(Order order : paymentOrder.getOrders()){
                //transactionService.createTransaction(order);
                Seller seller = sellerService.getSellerById(order.getSellerId());
                SellerReport report = sellerReportService.getSellerReport(seller);
                report.setTotalOrders(report.getTotalOrders() + 1);
                report.setTotalEarnings(report.getTotalEarnings() + order.getTotalSellingPrice());
                report.setTotalSales(report.getTotalSales() + order.getOrderItems().size());
                sellerReportService.updateSellerReport(report);
            }
        }
        ApiResponse response = new ApiResponse();
        response.setMessage("Payment successful");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<ApiResponse> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            Map<String, String> result = paymentService.vnpayReturn(params);

            ApiResponse response = new ApiResponse();
            response.setMessage(result.get("message"));

            if ("success".equals(result.get("status"))) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setMessage("Error processing payment return: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}