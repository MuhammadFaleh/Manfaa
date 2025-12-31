package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.ContractAgreementDTOIn;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.UserRepository;
import com.v1.manfaa.Service.ContractAgreementService;
import com.v1.manfaa.ValidationGroups.ValidationGroup1;
import com.v1.manfaa.ValidationGroups.ValidationGroup2;
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
    private final UserRepository userRepository;

    @GetMapping("/get-all")
    public ResponseEntity<?> getContracts(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(contractAgreementService.getContracts());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createContract(
            @Validated(ValidationGroup1.class) @RequestBody ContractAgreementDTOIn dto,
            @AuthenticationPrincipal User user) {

        // Fetch full user with company profile
        User fullUser = userRepository.findUserById(user.getId());

        if(fullUser == null || fullUser.getCompanyProfile() == null) {
            return ResponseEntity.status(400).body(new ApiResponse("User has no company profile"));
        }

        contractAgreementService.createContract(dto, fullUser.getCompanyProfile().getId());
        return ResponseEntity.status(200).body(new ApiResponse("Contract Created Successfully"));
    }

    @DeleteMapping("/delete/{contractId}")
    public ResponseEntity<?> deleteContract(
            @PathVariable Integer contractId,
            @AuthenticationPrincipal User user) {

        User fullUser = userRepository.findUserById(user.getId());

        if(fullUser == null || fullUser.getCompanyProfile() == null) {
            return ResponseEntity.status(400).body(new ApiResponse("User has no company profile"));
        }

        contractAgreementService.deleteContract(fullUser.getCompanyProfile().getId(), contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Deleted Successfully"));
    }

    @PutMapping("/accept/{contractId}")
    public ResponseEntity<?> acceptContract(
            @PathVariable Integer contractId,
            @AuthenticationPrincipal User user) {

        User fullUser = userRepository.findUserById(user.getId());

        if(fullUser == null || fullUser.getCompanyProfile() == null) {
            return ResponseEntity.status(400).body(new ApiResponse("User has no company profile"));
        }

        contractAgreementService.setAccepted(fullUser.getCompanyProfile().getId(), contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Accepted Successfully"));
    }

    @PostMapping("/reject/{contractId}")
    public ResponseEntity<?> rejectContract(
            @PathVariable Integer contractId,
            @AuthenticationPrincipal User user) {

        User fullUser = userRepository.findUserById(user.getId());

        if(fullUser == null || fullUser.getCompanyProfile() == null) {
            return ResponseEntity.status(400).body(new ApiResponse("User has no company profile"));
        }

        contractAgreementService.setRejected(fullUser.getCompanyProfile().getId(), contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Rejected Successfully"));
    }

    @PutMapping("/complete/{contractId}")
    public ResponseEntity<?> completeContract(
            @PathVariable Integer contractId,
            @Validated(ValidationGroup2.class) @RequestBody ContractAgreementDTOIn dto,
            @AuthenticationPrincipal User user) {

        User fullUser = userRepository.findUserById(user.getId());

        if(fullUser == null || fullUser.getCompanyProfile() == null) {
            return ResponseEntity.status(400).body(new ApiResponse("User has no company profile"));
        }

        contractAgreementService.complete(fullUser.getCompanyProfile().getId(), contractId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Contract Completed Successfully"));
    }
}