package com.v1.manfaa.testing;

import com.v1.manfaa.Model.CompanyCredit;
import com.v1.manfaa.Repository.CompanyCreditRepository;
import com.v1.manfaa.Service.CompanyCreditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyCreditServiceTest {

    @Mock
    private CompanyCreditRepository companyCreditRepository;

    @InjectMocks
    private CompanyCreditService companyCreditService;

    private CompanyCredit companyCredit1;
    private CompanyCredit companyCredit2;

    @BeforeEach
    void setUp() {
        companyCredit1 = new CompanyCredit();
        companyCredit1.setId(1);
        companyCredit1.setBalance(100.0);
        companyCredit1.setTotalEarned(500.0);
        companyCredit1.setTotalSpent(400.0);

        companyCredit2 = new CompanyCredit();
        companyCredit2.setId(2);
        companyCredit2.setBalance(200.0);
        companyCredit2.setTotalEarned(300.0);
        companyCredit2.setTotalSpent(100.0);
    }

    @Test
    void getAllCredits_ShouldReturnAllCredits() {
        // Arrange
        List<CompanyCredit> expectedCredits = Arrays.asList(companyCredit1, companyCredit2);
        when(companyCreditRepository.findAll()).thenReturn(expectedCredits);

        // Act
        List<CompanyCredit> actualCredits = companyCreditService.getAllCredits();

        // Assert
        assertNotNull(actualCredits);
        assertEquals(2, actualCredits.size());
        assertEquals(expectedCredits, actualCredits);
        verify(companyCreditRepository, times(1)).findAll();
    }

    @Test
    void getAllCredits_ShouldReturnEmptyList_WhenNoCreditsExist() {
        // Arrange
        when(companyCreditRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<CompanyCredit> actualCredits = companyCreditService.getAllCredits();

        // Assert
        assertNotNull(actualCredits);
        assertTrue(actualCredits.isEmpty());
        verify(companyCreditRepository, times(1)).findAll();
    }

    @Test
    void getMyCredit_ShouldReturnCredit_WhenCompanyProfileIdExists() {
        // Arrange
        Integer companyProfileId = 1;
        when(companyCreditRepository.findCompanyCreditByCompanyProfileId(companyProfileId))
                .thenReturn(companyCredit1);

        // Act
        CompanyCredit actualCredit = companyCreditService.getMyCredit(companyProfileId);

        // Assert
        assertNotNull(actualCredit);
        assertEquals(companyCredit1, actualCredit);
        assertEquals(100.0, actualCredit.getBalance());
        verify(companyCreditRepository, times(1)).findCompanyCreditByCompanyProfileId(companyProfileId);
    }

    @Test
    void getMyCredit_ShouldReturnNull_WhenCompanyProfileIdDoesNotExist() {
        // Arrange
        Integer companyProfileId = 999;
        when(companyCreditRepository.findCompanyCreditByCompanyProfileId(companyProfileId))
                .thenReturn(null);

        // Act
        CompanyCredit actualCredit = companyCreditService.getMyCredit(companyProfileId);

        // Assert
        assertNull(actualCredit);
        verify(companyCreditRepository, times(1)).findCompanyCreditByCompanyProfileId(companyProfileId);
    }
}
