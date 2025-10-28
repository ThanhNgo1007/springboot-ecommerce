package com.furniture.modal;

import com.furniture.domain.PaymentStatus;
import lombok.Data;

@Data
public class PaymentDetails {
    private String paymentId;
    private String paymentLinkId;
    private String paymentLinkRefernceId;
    private String paymentLinkStatus;
    private String paymentIdZWSP;
    private PaymentStatus status;
}
