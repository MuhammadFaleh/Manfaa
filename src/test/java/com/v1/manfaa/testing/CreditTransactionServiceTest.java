package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.Out.CreditTransactionDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditTransactionServiceTest {

    @Mock
    private CreditTransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContractAgreementRepository contractAgreementRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private CompanyCreditRepository companyCreditRepository;

    @InjectMocks
    private CreditTransactionService creditTransactionService;

    private User admin;
    private CompanyProfile company1;
    private CompanyProfile company2;
    private CompanyCredit credit1;
    private CompanyCredit credit2;
    private CreditTransaction transaction;
    private ContractAgreement contract;
    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1);
        admin.setRole("ADMIN");

        company1 = new CompanyProfile();
        company1.setId(1);
        company1.setName("Company 1");

        company2 = new CompanyProfile();
        company2.setId(2);
        company2.setName("Company 2");

        credit1 = new CompanyCredit();
        credit1.setId(1);
        credit1.setBalance(100.0);
        credit1.setTotalEarned(500.0);
        credit1.setTotalSpent(400.0);
        credit1.setCompanyProfile(company1);

        credit2 = new CompanyCredit();
        credit2.setId(2);
        credit2.setBalance(200.0);
        credit2.setTotalEarned(300.0);
        credit2.setTotalSpent(100.0);
        credit2.setCompanyProfile(company2);

        company1.setCompanyCredit(credit1);
        company2.setCompanyCredit(credit2);

        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1);
        serviceRequest.setCompanyProfile(company1);

        contract = new ContractAgreement();
        contract.setId(1);
        contract.setStatus("COMPLETED");
        contract.setExchangeType("TOKENS");
        contract.setServiceRequest(serviceRequest);

        transaction = new CreditTransaction();
        transaction.setId(1);
        transaction.setAmount(50.0);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus("PENDING");
        transaction.setPayingCompany(credit1);
        transaction.setPaidCompany(credit2);
        transaction.setContractAgreement(contract);

        contract.setCreditTransaction(transaction);
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction));

        // Act
        List<CreditTransactionDTOOut> result = creditTransactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(50.0, result.get(0).getAmount());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getCompanyTransactionsForAdmin_ShouldReturnTransactions_WhenAdminExists() {
        // Arrange
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(transactionRepository.findByPayingCompanyIdOrPaidCompanyId(1, 1))
                .thenReturn(Arrays.asList(transaction));

        // Act
        List<CreditTransactionDTOOut> result = 
                creditTransactionService.getCompanyTransactionsForAdmin(admin.getId(), 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findById(admin.getId());
    }

    @Test
    void getCompanyTransactionsForAdmin_ShouldThrowException_WhenAdminNotFound() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> creditTransactionService.getCompanyTransactionsForAdmin(999, 1));

        assertEquals("Admin not found", exception.getMessage());
        verify(transactionRepository, never()).findByPayingCompanyIdOrPaidCompanyId(any(), any());
    }

    @Test
    void getTransactionsByCompanyId_ShouldReturnTransactions() {
        // Arrange
        when(transactionRepository.findByPayingCompanyIdOrPaidCompanyId(1, 1))
                .thenReturn(Arrays.asList(transaction));

        // Act
        List<CreditTransactionDTOOut> result = 
                creditTransactionService.getTransactionsByCompanyId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByPayingCompanyIdOrPaidCompanyId(1, 1);
    }

    @Test
    void refundCredit_ShouldRefundCredit_WhenContractIsCompleted() {
        // Arrange
        when(contractAgreementRepository.findContractAgreementById(contract.getId()))
                .thenReturn(contract);

        // Act
        creditTransactionService.refundCredit(contract.getId());

        // Assert
        assertEquals("CANCELED", transaction.getStatus());
        assertEquals(150.0, credit1.getBalance()); // 100 + 50
        verify(transactionRepository, times(1)).save(transaction);
        verify(companyCreditRepository, times(1)).save(credit1);
        verify(companyProfileRepository, times(1)).save(company1);
    }

    @Test
    void refundCredit_ShouldThrowException_WhenContractNotFound() {
        // Arrange
        when(contractAgreementRepository.findContractAgreementById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> creditTransactionService.refundCredit(999));

        assertEquals("no contract found", exception.getMessage());
    }

    @Test
    void refundCredit_ShouldThrowException_WhenContractIsNotClosed() {
        // Arrange
        contract.setStatus("ACTIVE");
        when(contractAgreementRepository.findContractAgreementById(contract.getId()))
                .thenReturn(contract);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> creditTransactionService.refundCredit(contract.getId()));

        assertEquals("contract is not yet closed", exception.getMessage());
    }

    @Test
    void refundCredit_ShouldThrowException_WhenExchangeTypeIsNotTokens() {
        // Arrange
        contract.setExchangeType("BARTER");
        when(contractAgreementRepository.findContractAgreementById(contract.getId()))
                .thenReturn(contract);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> creditTransactionService.refundCredit(contract.getId()));

        assertEquals("transaction type is not token based", exception.getMessage());
    }

    @Test
    void refundCredit_ShouldThrowException_WhenAlreadyRefunded() {
        // Arrange
        transaction.setStatus("CANCELED");
        when(contractAgreementRepository.findContractAgreementById(contract.getId()))
                .thenReturn(contract);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> creditTransactionService.refundCredit(contract.getId()));

        assertEquals("already refunded", exception.getMessage());
    }

    @Test
    void refundCredit_ShouldRefundCredit_WhenContractIsDisputed() {
        // Arrange
        contract.setStatus("DISPUTED");
        when(contractAgreementRepository.findContractAgreementById(contract.getId()))
                .thenReturn(contract);

        // Act
        creditTransactionService.refundCredit(contract.getId());

        // Assert
        assertEquals("CANCELED", transaction.getStatus());
        assertEquals(150.0, credit1.getBalance());
    }

    @Test
    void addCreditToUser_ShouldAddCredit_WhenCompanyExists() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(company1);

        // Act
        creditTransactionService.addCreditToUser(1, 50.0);

        // Assert
        assertEquals(150.0, credit1.getBalance()); // 100 + 50
        verify(companyCreditRepository, times(1)).save(credit1);
    }

    @Test
    void addCreditToUser_ShouldThrowException_WhenCompanyNotFound() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(null);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class,
                () -> creditTransactionService.addCreditToUser(999, 50.0));

        assertEquals("company not found", exception.getMessage());
        verify(companyCreditRepository, never()).save(any(CompanyCredit.class));
    }

    @Test
    void addCreditToUser_ShouldHandleLargeAmounts() {
        // Arrange
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(company1);

        // Act
        creditTransactionService.addCreditToUser(1, 10000.0);

        // Assert
        assertEquals(10100.0, credit1.getBalance());
    }

    @Test
    void convertToDtoOut_ShouldConvertTransactionsToDTO() {
        // Arrange
        List<CreditTransaction> transactions = Arrays.asList(transaction);

        // Act
        List<CreditTransactionDTOOut> result = 
                creditTransactionService.convertToDtoOut(transactions);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CreditTransactionDTOOut dto = result.get(0);
        assertEquals(50.0, dto.getAmount());
        assertEquals(1, dto.getContractId());
        assertEquals("Company 1", dto.getPayingCompanyName());
        assertEquals("Company 2", dto.getPaidCompanyName());
    }

    @Test
    void convertToDtoOut_ShouldReturnEmptyList_WhenGivenEmptyList() {
        // Act
        List<CreditTransactionDTOOut> result = 
                creditTransactionService.convertToDtoOut(Arrays.asList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
