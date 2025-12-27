package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.CompanyProfileDTOIn;
import com.v1.manfaa.DTO.In.RegisterDTOIn;
import com.v1.manfaa.DTO.Out.CompanyProfileDTOOut;
import com.v1.manfaa.Model.CompanyCredit;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.CompanyCreditRepository;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;
    private final CompanyCreditRepository companyCreditRepository;

    public List<CompanyProfileDTOOut> getAllCompanyProfiles() {
        return convertToDtoOut(companyProfileRepository.findAll());
    }

    @Transactional
    public void registerCompany(RegisterDTOIn dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        String hash = new BCryptPasswordEncoder().encode(dto.getPassword());
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(hash);
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhone_Number(dto.getPhoneNumber());
        user.setRole("COMPANY");

        CompanyProfile company = new CompanyProfile();
        company.setName(dto.getCompanyName());
        company.setIndustry(dto.getIndustry());
        company.setTeamSize(dto.getTeamSize());
        company.setDescription(dto.getDescription());
        company.setCreatedAt(LocalDateTime.now());
        company.setIsSubscriber(false);

        CompanyCredit companyCredit = new CompanyCredit();
        companyCredit.setBalance(0);
        companyCredit.setTotalEarned(0);
        companyCredit.setTotalSpent(0);

        company.setCompanyCredit(companyCredit);
        company.setUser(user);
        user.setCompanyProfile(company);
        userRepository.save(user);
    }

    public void updateCompanyProfile(Integer companyProfileId, CompanyProfileDTOIn dto) {
        CompanyProfile oldCompany = companyProfileRepository.findCompanyProfileById(companyProfileId);
        if (oldCompany == null)
            throw new ApiException("Company Profile was not Found");

        oldCompany.setName(dto.getName());
        oldCompany.setIndustry(dto.getIndustry());
        oldCompany.setTeamSize(dto.getTeamSize());
        oldCompany.setDescription(dto.getDescription());

        companyProfileRepository.save(oldCompany);
    }

    @Transactional
    public void deleteCompanyProfile(Integer companyProfileId) {

        CompanyProfile company = companyProfileRepository.findById(companyProfileId)
                .orElseThrow(() -> new ApiException("Company Profile not found"));

        User user = company.getUser();
        CompanyCredit companyCredit = company.getCompanyCredit();

        companyCredit.setCompanyProfile(null);
        user.setCompanyProfile(null);

        company.setUser(null);
        company.setCompanyCredit(null);

        companyProfileRepository.delete(company);
        companyCreditRepository.delete(companyCredit);
        userRepository.delete(user);
    }


    public List<CompanyProfileDTOOut> convertToDtoOut(List<CompanyProfile> profiles) {
        return profiles.stream()
                .map(profile -> new CompanyProfileDTOOut(
                        profile.getName(),
                        profile.getIndustry(),
                        profile.getTeamSize(),
                        profile.getDescription(),
                        profile.getCreatedAt(),
                        profile.getIsSubscriber()
                ))
                .toList();
    }
}
