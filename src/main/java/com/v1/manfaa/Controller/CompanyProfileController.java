package com.v1.manfaa.Controller;

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
    public ResponseEntity<?> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanyProfiles());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody RegisterDTOIn dto) {
        companyService.registerCompany(dto);
        return ResponseEntity.ok("Company Registered Successfully");
    }

    @PutMapping("/update/{companyProfileId}")
    public ResponseEntity<?> updateCompanyProfile(@PathVariable Integer companyProfileId,
                                                  @Valid @RequestBody CompanyProfileDTOIn dto) {
        companyService.updateCompanyProfile(companyProfileId, dto);
        return ResponseEntity.ok("Company Updated Successfully");
    }

    @DeleteMapping("/delete/{companyProfileId}")
    public ResponseEntity<?> deleteCompanyProfile(@PathVariable Integer companyProfileId) {
        companyService.deleteCompanyProfile(companyProfileId);
        return ResponseEntity.ok("Company Deleted Successfully");
    }
}

