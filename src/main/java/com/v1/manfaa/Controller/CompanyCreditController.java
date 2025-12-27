package com.v1.manfaa.Controller;

import com.v1.manfaa.Service.CompanyCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CompanyCreditController {

    private final CompanyCreditService companyCreditService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCredits() {
        return ResponseEntity.ok(companyCreditService.getAllCredits());
    }
}
