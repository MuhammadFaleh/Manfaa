package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.CreditAdminDTO;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.CreditTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class CreditTransactionController {

    private final CreditTransactionService creditTransactionService;

    @GetMapping("/get-all") // admin
    public ResponseEntity<?> getAllTransactions(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(creditTransactionService.getAllTransactions());
    }

    @PostMapping("/add-balance") // admin
    public ResponseEntity<?> addBalance(@AuthenticationPrincipal User user, @Valid @RequestBody CreditAdminDTO dto){
        creditTransactionService.addCreditToUser(dto.getUserId(),dto.getAmount());
        return ResponseEntity.status(200).body(new ApiResponse("funds added successfully"));
    }

    @PutMapping("/refund/{contractId}") // admin
    public ResponseEntity<?> refund(@PathVariable Integer contractId, @AuthenticationPrincipal User user){
        creditTransactionService.refundCredit(contractId);
        return ResponseEntity.status(200).body(new ApiResponse("refund done successfully"));
    }

    @GetMapping("/get-by-companyId/{companyId}") // admin
    public ResponseEntity<?> getCompanyTransactionsAdmin(@AuthenticationPrincipal User user,
                                                         @PathVariable Integer companyId) {
        return ResponseEntity.status(200).body(creditTransactionService.getCompanyTransactionsForAdmin(user.getId(), companyId));
    }

    @GetMapping("/get-my-transactions") // user
    public ResponseEntity<?> getCompanyTransactions(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(creditTransactionService.getTransactionsByCompanyId(user.getId()));
    }
}
