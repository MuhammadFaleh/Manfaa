package com.v1.manfaa.Controller;

import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.CompanyCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CompanyCreditController {

    private final CompanyCreditService companyCreditService;

    @GetMapping("/get-all") // admin
    public ResponseEntity<?> getAllCredits(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(companyCreditService.getAllCredits());
    }

    @GetMapping("/get-my-credits") //user
    public ResponseEntity<?> getMyCredit(@AuthenticationPrincipal User user){
        return ResponseEntity.status(200).body(companyCreditService.getMyCredit(user.getId()));
    }
}
