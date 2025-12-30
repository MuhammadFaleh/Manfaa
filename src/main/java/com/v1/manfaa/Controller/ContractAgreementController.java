package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ContractAgreementDTOIn;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.ContractAgreementService;
import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractAgreementController {

    private final ContractAgreementService contractAgreementService;

    @GetMapping("/get-all") // admin
    public ResponseEntity<?> getContracts(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(contractAgreementService.getContracts());
    }

    @PostMapping("/create") // user
    public ResponseEntity<?> createContract(@Validated(ValidationGroup1.class) @RequestBody ContractAgreementDTOIn dto,
                                            @AuthenticationPrincipal User user) {
        contractAgreementService.createContract(dto, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Contract Created Successfully"));
    }

    @DeleteMapping("/delete/{contractId}") // user
    public ResponseEntity<?> deleteContract(@PathVariable Integer contractId,
                                            @AuthenticationPrincipal User user) {
        contractAgreementService.deleteContract(user.getId(), contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Deleted Successfully"));
    }

    @PutMapping("/accept/{contractId}") // user
    public ResponseEntity<?> acceptContract(@PathVariable Integer contractId,
                                            @AuthenticationPrincipal User user) {
        contractAgreementService.setAccepted(user.getId(), contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Accepted Successfully"));
    }

    @PutMapping("/reject/{contractId}") // user
    public ResponseEntity<?> rejectContract(@PathVariable Integer contractId,
                                            @AuthenticationPrincipal User user) {
        contractAgreementService.setRejected(user.getId(), contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Rejected Successfully"));
    }

    @PutMapping("/complete/{contractId}") // user
    public ResponseEntity<?> completeContract(@PathVariable Integer contractId,
                                              @Validated(ValidationGroup2.class) @RequestBody ContractAgreementDTOIn dto,
                                            @AuthenticationPrincipal User user) {
        contractAgreementService.complete(user.getId(), contractId,dto);
        return ResponseEntity.status(200).body(new ApiResponse("Contract completed Successfully"));
    }
}