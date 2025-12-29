package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.CompanyProfileDTOIn;
import com.v1.manfaa.DTO.In.RegisterDTOIn;
import com.v1.manfaa.DTO.Out.CompanyProfileDTOOut;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.CompanyProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCompanies(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(companyService.getAllCompanyProfiles());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody RegisterDTOIn dto) {
        companyService.registerCompany(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Company Registered Successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCompanyProfile(@Valid @RequestBody CompanyProfileDTOIn dto,
                                                  @AuthenticationPrincipal User user) {
        companyService.updateCompanyProfile(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Company Updated Successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCompanyProfile(@AuthenticationPrincipal User user) {
        companyService.deleteCompanyProfile(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Company Deleted Successfully"));
    }

    @GetMapping("/get-companies-full")
    public ResponseEntity<?> getCompaniesFull(@AuthenticationPrincipal User user){
        return ResponseEntity.status(200).body(companyService.getAllCompaniesFullDetails());
    }

    @GetMapping("/get-company-full")
    public ResponseEntity<?> getCompanyFull(@AuthenticationPrincipal User user){
        return ResponseEntity.status(200).body(companyService.getCompanyDetails(user.getId()));
    }

    @GetMapping("/get-company-id-full/{companyId}")
    public ResponseEntity<?> getCompanyByIdFull(@PathVariable Integer companyId, @AuthenticationPrincipal User user){
        return ResponseEntity.status(200).body(companyService.getCompanyById(companyId));
    }
}

