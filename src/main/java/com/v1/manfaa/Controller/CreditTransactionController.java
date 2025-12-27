package com.v1.manfaa.Controller;

import com.v1.manfaa.DTO.Out.CreditTransactionDTOOut;
import com.v1.manfaa.Service.CreditTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class CreditTransactionController {

    private final CreditTransactionService creditTransactionService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(creditTransactionService.getAllTransactions());
    }

    @GetMapping("/admin/{adminId}/company/{companyId}/transactions")
    public ResponseEntity<List<CreditTransactionDTOOut>> getCompanyTransactionsAdmin(@PathVariable Integer adminId,
                                                                                     @PathVariable Integer companyId) {
        return ResponseEntity.ok(creditTransactionService.getCompanyTransactionsForAdmin(adminId, companyId));
    }

    @GetMapping("/company/{companyId}/transactions")
    public ResponseEntity<List<CreditTransactionDTOOut>> getCompanyTransactions(@PathVariable Integer companyId) {
        return ResponseEntity.ok(creditTransactionService.getTransactionsByCompanyId(companyId));
    }
}
