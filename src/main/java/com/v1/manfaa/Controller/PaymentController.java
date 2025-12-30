package com.v1.manfaa.Controller;

import com.v1.manfaa.Model.PaymentRequest;
import com.v1.manfaa.Service.PaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentRequestService paymentService;




}