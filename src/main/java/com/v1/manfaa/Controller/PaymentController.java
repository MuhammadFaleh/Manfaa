package com.v1.manfaa.Controller;

import com.v1.manfaa.DTO.In.PaymentDTOIn;
import com.v1.manfaa.DTO.Out.PaymentDTOOut;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay") // user
    public ResponseEntity<?> payTest(@RequestBody @Valid PaymentDTOIn dto, @AuthenticationPrincipal User user){
        ResponseEntity<PaymentDTOOut> response = paymentService.processPayment(dto, user.getId());
        return ResponseEntity.status(200).body(response.getBody());
    }

    @GetMapping("/callback") // all
    public ResponseEntity<?> callback(@RequestParam("id") String id){
        ResponseEntity<PaymentDTOOut> response = paymentService.getPayment(id);
        return ResponseEntity.status(200).body(response.getBody());
    }
}