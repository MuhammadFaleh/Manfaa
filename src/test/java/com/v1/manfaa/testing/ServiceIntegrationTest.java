package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ContractAgreementDTOIn;
import com.v1.manfaa.DTO.In.ServiceBidDTOIn;
import com.v1.manfaa.DTO.In.ServiceRequestDTOIn;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import com.v1.manfaa.Service.ContractAgreementService;
import com.v1.manfaa.Service.EmailService;
import com.v1.manfaa.Service.ServiceBidService;
import com.v1.manfaa.Service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceIntegrationTest {

    // Service Request mocks
    @Mock
    private ServiceRequestRepository serviceRequestRepository;
    @Mock
    private CompanyProfileRepository companyProfileRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ServiceBidRepository serviceBidRepository;

    // Contract Agreement mocks
    @Mock
    private ContractAgreementRepository contractAgreementRepository;
    @Mock
    private CompanyCreditRepository companyCreditRepository;
    @Mock
    private CreditTransactionRepository creditTransactionRepository;

    // Email service
    @Mock
    private EmailService emailService;

    @InjectMocks
    private ServiceRequestService serviceRequestService;

    @InjectMocks
    private ServiceBidService serviceBidService;

    @InjectMocks
    private ContractAgreementService contractAgreementService;

    // Test data
    private CompanyProfile requesterCompany;
    private CompanyProfile providerCompany;
    private Category category;
    private ServiceRequest serviceRequest;
    private ServiceBid serviceBid;
    private ContractAgreement contractAgreement;
    private CompanyCredit requesterCredit;
    private CompanyCredit providerCredit;
    private CreditTransaction creditTransaction;
    private User requesterUser;
    private User providerUser;

    @BeforeEach
    void setUp() {
        // Setup Users
        requesterUser = new User();
        requesterUser.setId(1);
        requesterUser.setEmail("requester@example.com");
        requesterUser.setRole("COMPANY");

        providerUser = new User();
        providerUser.setId(2);
        providerUser.setEmail("provider@example.com");
        providerUser.setRole("COMPANY");

        // Setup Company Credits
        requesterCredit = new CompanyCredit();
        requesterCredit.setId(1);
        requesterCredit.setBalance(10000.0);

        providerCredit = new CompanyCredit();
        providerCredit.setId(2);
        providerCredit.setBalance(5000.0);

        // Setup Requester Company
        requesterCompany = new CompanyProfile();
        requesterCompany.setId(1);
        requesterCompany.setName("Tech Solutions Inc");
        requesterCompany.setIndustry("Technology");
        requesterCompany.setIsSubscriber(true);
        requesterCompany.setUser(requesterUser);
        requesterCompany.setCompanyCredit(requesterCredit);
        requesterCompany.setServiceRequest(new HashSet<>());
        requesterCompany.setServiceBid(new HashSet<>());
        requesterCompany.setRequesterContractAgreement(new HashSet<>());
        requesterCompany.setProviderContractAgreement(new HashSet<>());

        requesterCredit.setCompanyProfile(requesterCompany);

        // Setup Provider Company
        providerCompany = new CompanyProfile();
        providerCompany.setId(2);
        providerCompany.setName("Marketing Experts LLC");
        providerCompany.setIndustry("Marketing");
        providerCompany.setIsSubscriber(false);
        providerCompany.setUser(providerUser);
        providerCompany.setCompanyCredit(providerCredit);
        providerCompany.setServiceRequest(new HashSet<>());
        providerCompany.setServiceBid(new HashSet<>());
        providerCompany.setRequesterContractAgreement(new HashSet<>());
        providerCompany.setProviderContractAgreement(new HashSet<>());

        providerCredit.setCompanyProfile(providerCompany);

        // Setup Category
        category = new Category();
        category.setId(1);
        category.setName("Web Development");
        category.setServiceRequest(new HashSet<>());

        // Setup Service Request
        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1);
        serviceRequest.setTitle("Need E-commerce Website");
        serviceRequest.setDescription("Looking for a professional e-commerce website");
        serviceRequest.setDeliverables("Fully functional website with payment integration");
        serviceRequest.setProposedStartDate(LocalDate.now().plusDays(5));
        serviceRequest.setProposedEndDate(LocalDate.now().plusDays(35));
        serviceRequest.setExchangeType("TOKENS");
        serviceRequest.setTokenAmount(5000.0);
        serviceRequest.setStatus("OPEN");
        serviceRequest.setCreatedAt(LocalDateTime.now());
        serviceRequest.setCategory(category);
        serviceRequest.setCompanyProfile(requesterCompany);
        serviceRequest.setServiceBid(new HashSet<>());

        // Setup Service Bid
        serviceBid = new ServiceBid();
        serviceBid.setId(1);
        serviceBid.setDescription("We can deliver a professional e-commerce solution");
        serviceBid.setDeliverables("Complete website with admin panel, payment gateway, inventory management");
        serviceBid.setEstimatedHours(200.0);
        serviceBid.setProposedStartDate(LocalDate.now().plusDays(5));
        serviceBid.setProposedEndDate(LocalDate.now().plusDays(35));
        serviceBid.setPaymentMethod("TOKENS");
        serviceBid.setTokenAmount(5000.0);
        serviceBid.setStatus("PENDING");
        serviceBid.setCreatedAt(LocalDateTime.now());
        serviceBid.setServiceRequest(serviceRequest);
        serviceBid.setCompanyProfile(providerCompany);

        // Setup Credit Transaction
        creditTransaction = new CreditTransaction();
        creditTransaction.setId(1);
        creditTransaction.setAmount(5000.0);
        creditTransaction.setCreatedAt(LocalDateTime.now());
        creditTransaction.setStatus("PENDING");
        creditTransaction.setPayingCompany(requesterCredit);
        creditTransaction.setPaidCompany(providerCredit);

        // Setup Contract Agreement
        contractAgreement = new ContractAgreement();
        contractAgreement.setId(1);
        contractAgreement.setStartDate(LocalDate.now().plusDays(5));
        contractAgreement.setEndDate(LocalDate.now().plusDays(35));
        contractAgreement.setIsExtended(false);
        contractAgreement.setExchangeType("TOKENS");
        contractAgreement.setTokenAmount(5000.0);
        contractAgreement.setStatus("PENDING");
        contractAgreement.setCreatedAt(LocalDateTime.now());
        contractAgreement.setFirstPartyAgreement("ACCEPTED");
        contractAgreement.setSecondPartyAgreement("PENDING");
        contractAgreement.setServiceRequest(serviceRequest);
        contractAgreement.setServiceBid(serviceBid);
        contractAgreement.setProviderCompanyProfile(providerCompany);
        contractAgreement.setRequesterCompanyProfile(requesterCompany);
        contractAgreement.setCreditTransaction(creditTransaction);

        creditTransaction.setContractAgreement(contractAgreement);
    }

    // ==================== FULL WORKFLOW INTEGRATION TESTS ====================

    @Test
    void fullWorkflow_TokenBased_Success() {
        // Step 1: Create Service Request
        ServiceRequestDTOIn requestDTO = new ServiceRequestDTOIn(null, "Need E-commerce Website", "Looking for a professional e-commerce website", "Fully functional website", LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(35).toString(), 5000.0, 1, null);
        requestDTO.setTitle("Need E-commerce Website");
        requestDTO.setDescription("Looking for a professional e-commerce website");
        requestDTO.setDeliverables("Fully functional website");
        requestDTO.setProposedStartDate(LocalDate.now().plusDays(5).toString());
        requestDTO.setProposedEndDate(LocalDate.now().plusDays(35).toString());
        requestDTO.setTokenAmount(5000.0);
        requestDTO.setCategory(1);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(categoryRepository.findCategoryById(1)).thenReturn(category);

        serviceRequestService.createTokenRequest(requestDTO, 1);

        verify(serviceRequestRepository, times(1)).save(any(ServiceRequest.class));

        // Step 2: Create Service Bid
        ServiceBidDTOIn bidDTO = new ServiceBidDTOIn(1, "We can deliver a professional solution", null, "Complete website with admin panel", 200.0, LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(35).toString(), "TOKENS", 5000.0);


        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceBidService.createBid(2, 1, bidDTO);

        verify(serviceBidRepository, times(1)).save(any(ServiceBid.class));

        // Step 3: Accept Service Bid
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        serviceBidService.acceptServiceBid(1, 1);

        verify(serviceBidRepository, times(1)).save(serviceBid);
        assertEquals("ACCEPTED", serviceBid.getStatus());
        assertEquals("CLOSED", serviceRequest.getStatus());

        // Step 4: Create Contract
        ContractAgreementDTOIn contractDTO = new ContractAgreementDTOIn(null, 1, 1, "");
        contractDTO.setRequestId(1);
        contractDTO.setBidId(1);

        serviceRequest.setStatus("CLOSED");
        serviceBid.setStatus("ACCEPTED");

        when(contractAgreementRepository.findContractAgreementByServiceBidId(1)).thenReturn(new ArrayList<>());
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        contractAgreementService.createContract(contractDTO, 1);

        verify(contractAgreementRepository, times(1)).save(any(ContractAgreement.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());

        // Step 5: Accept Contract by Provider
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);

        contractAgreementService.setAccepted(2, 1);

        verify(contractAgreementRepository, times(1)).save(contractAgreement);
        assertEquals("ACTIVE", contractAgreement.getStatus());
    }

    @Test
    void fullWorkflow_BarterBased_Success() {
        // Setup for Barter
        serviceRequest.setExchangeType("BARTER");
        serviceBid.setPaymentMethod("BARTER");
        contractAgreement.setExchangeType("BARTER");

        ServiceRequestDTOIn requestDTO = new ServiceRequestDTOIn(null, "Need Marketing Services", "Looking for social media marketing", "Social media campaign", LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(35).toString(), null, 1, null);


        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(categoryRepository.findCategoryById(1)).thenReturn(category);

        serviceRequestService.createBarterRequest(requestDTO, 1);

        verify(serviceRequestRepository, times(1)).save(any(ServiceRequest.class));
    }

    // ==================== SERVICE REQUEST TESTS ====================

    @Test
    void createTokenRequest_Success() {
        ServiceRequestDTOIn dto = new ServiceRequestDTOIn(null, "Test Request", "Test Description", "Test Deliverables", LocalDate.now().plusDays(1).toString(), LocalDate.now().plusDays(10).toString(), 1000.0, 1, null);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(categoryRepository.findCategoryById(1)).thenReturn(category);

        serviceRequestService.createTokenRequest(dto, 1);

        verify(serviceRequestRepository, times(1)).save(any(ServiceRequest.class));
    }

    @Test
    void createTokenRequest_ThrowsException_InsufficientCredit() {
        requesterCredit.setBalance(100.0);

        ServiceRequestDTOIn dto = new ServiceRequestDTOIn(null, null, null, null, null, null, 5000.0, 1, null);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(categoryRepository.findCategoryById(1)).thenReturn(category);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceRequestService.createTokenRequest(dto, 1);
        });

        assertEquals("not enough credit", exception.getMessage());
        verify(serviceRequestRepository, never()).save(any());
    }

    @Test
    void updateRequest_Success() {
        ServiceRequestDTOIn dto = new ServiceRequestDTOIn(null, "Updated Title", "Updated Description", "Updated Deliverables", null, null, 6000.0, null, null);

        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceRequestService.updateRequest(dto, 1, 1);

        verify(serviceRequestRepository, times(1)).save(serviceRequest);
    }

    @Test
    void updateRequest_ThrowsException_RequestNotOpen() {
        serviceRequest.setStatus("CLOSED");

        ServiceRequestDTOIn dto = new ServiceRequestDTOIn(null, null, null, null, null, null, null, null, null);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceRequestService.updateRequest(dto, 1, 1);
        });

        assertEquals("service request is closed or canceled and can't be updated", exception.getMessage());
    }

    @Test
    void deleteRequest_Success() {
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceRequestService.deleteRequest(1, 1);

        verify(serviceRequestRepository, times(1)).delete(serviceRequest);
    }

    @Test
    void deleteRequest_ThrowsException_UnauthorizedUser() {
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceRequestService.deleteRequest(1, 999);
        });

        assertEquals("service request not found", exception.getMessage());
        verify(serviceRequestRepository, never()).delete(any());
    }

    @Test
    void searchServiceRequests_Success() {
        List<ServiceRequest> requests = Arrays.asList(serviceRequest);

        when(serviceRequestRepository.searchServiceRequestsByKeyword("E-commerce")).thenReturn(requests);

        var result = serviceRequestService.searchServiceRequests("E-commerce");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getServiceRequestsByExchangeType_Success() {
        List<ServiceRequest> requests = Arrays.asList(serviceRequest);

        when(serviceRequestRepository.findServiceRequestsByExchangeType("TOKENS")).thenReturn(requests);

        var result = serviceRequestService.getServiceRequestsByExchangeType("TOKENS");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getServiceRequestsByExchangeType_ThrowsException_InvalidType() {
        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceRequestService.getServiceRequestsByExchangeType("INVALID");
        });

        assertEquals("Invalid exchange type. Must be TOKENS, BARTER, or EITHER", exception.getMessage());
    }

// ==================== SERVICE BID TESTS ====================

    @Test
    void createBid_Success_WithEitherExchangeType() {
        serviceRequest.setExchangeType("EITHER");

        ServiceBidDTOIn dto = new ServiceBidDTOIn(null, "Test Bid", null, "Test Deliverables", 100.0, LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(20).toString(), "BARTER", 3000.0);

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceBidService.createBid(2, 1, dto);

        verify(serviceBidRepository, times(1)).save(any(ServiceBid.class));
    }

    @Test
    void createBid_ThrowsException_ExchangeTypeMismatch() {
        serviceRequest.setExchangeType("TOKENS");

        ServiceBidDTOIn dto = new ServiceBidDTOIn(null, "Test Bid", null, null, 100.0, LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(20).toString(), "BARTER", null);

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(2, 1, dto);
        });

        assertEquals("exchange type not the same as the request", exception.getMessage());
    }

    @Test
    void createBid_ThrowsException_InvalidDates() {
        ServiceBidDTOIn dto = new ServiceBidDTOIn(null, null, null, null, 1000.0, LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(6).toString(), "TOKENS", null);

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(2, 1, dto);
        });

        assertEquals("wrong dates expected hours and date don't make sense", exception.getMessage());
    }

    @Test
    void rejectServiceBid_Success_WithEmail() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        serviceBidService.rejectServiceBid(1, 1, "Not suitable for our needs");

        verify(serviceBidRepository, times(1)).save(serviceBid);
        verify(emailService, times(1)).sendEmail(
                eq("provider@example.com"),
                eq("Service Bid Rejected"),
                contains("Not suitable for our needs")
        );
        assertEquals("REJECTED", serviceBid.getStatus());
    }

// ==================== CONTRACT AGREEMENT TESTS ====================

    @Test
    void createContract_Success_WithTokenHold() {
        serviceRequest.setStatus("CLOSED");
        serviceBid.setStatus("ACCEPTED");

        ContractAgreementDTOIn dto = new ContractAgreementDTOIn(null, 1, 1, null);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);
        when(contractAgreementRepository.findContractAgreementByServiceBidId(1)).thenReturn(new ArrayList<>());
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        contractAgreementService.createContract(dto, 1);

        verify(contractAgreementRepository, times(1)).save(any(ContractAgreement.class));
        verify(creditTransactionRepository, times(1)).save(any(CreditTransaction.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        assertEquals(5000.0, requesterCredit.getBalance());
    }

    @Test
    void createContract_ThrowsException_RequestNotClosed() {
        serviceRequest.setStatus("OPEN");
        serviceBid.setStatus("ACCEPTED");

        ContractAgreementDTOIn dto = new ContractAgreementDTOIn(null, 1, 1, null);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            contractAgreementService.createContract(dto, 1);
        });

        assertEquals("Request must be CLOSED to create contract", exception.getMessage());
    }

    @Test
    void createContract_ThrowsException_ContractAlreadyExists() {
        serviceRequest.setStatus("CLOSED");
        serviceBid.setStatus("ACCEPTED");

        ContractAgreementDTOIn dto = new ContractAgreementDTOIn(null, 1, 1, null);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);
        when(contractAgreementRepository.findContractAgreementByServiceBidId(1))
                .thenReturn(Arrays.asList(contractAgreement));

        ApiException exception = assertThrows(ApiException.class, () -> {
            contractAgreementService.createContract(dto, 1);
        });

        assertEquals("Contract already exists for this bid", exception.getMessage());
    }

    @Test
    void setAccepted_Success_ActivatesContract() {
        contractAgreement.setFirstPartyAgreement("ACCEPTED");
        contractAgreement.setSecondPartyAgreement("PENDING");

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        contractAgreementService.setAccepted(2, 1);

        verify(contractAgreementRepository, times(1)).save(contractAgreement);
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
        assertEquals("ACTIVE", contractAgreement.getStatus());
        assertEquals("ACCEPTED", contractAgreement.getSecondPartyAgreement());
    }

    @Test
    void setRejected_Success_ReleasesTokens() {
        contractAgreement.setExchangeType("TOKENS");
        contractAgreement.setSecondPartyAgreement("PENDING");

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        contractAgreementService.setRejected(2, 1);

        verify(contractAgreementRepository, times(1)).save(contractAgreement);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(creditTransactionRepository, times(1)).save(creditTransaction);
        assertEquals("CANCELLED", contractAgreement.getStatus());
        assertEquals("OPEN", serviceRequest.getStatus());
        assertEquals(15000.0, requesterCredit.getBalance()); // Released back
    }

    @Test
    void complete_Success_TransfersTokens() {
        contractAgreement.setStatus("ACTIVE");
        contractAgreement.setFirstPartyAgreement("DELIVERED");
        contractAgreement.setSecondPartyAgreement("PENDING");
        contractAgreement.setExchangeType("TOKENS");

        ContractAgreementDTOIn dto = new ContractAgreementDTOIn(null, null, null, "Project delivered successfully");

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        contractAgreementService.complete(1, 2, dto);

        verify(contractAgreementRepository, times(1)).save(contractAgreement);
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
        verify(creditTransactionRepository, times(1)).save(creditTransaction);
        assertEquals("COMPLETED", contractAgreement.getStatus());
        assertEquals(10000.0, providerCredit.getBalance()); // Tokens transferred
        assertEquals("ACCEPTED", creditTransaction.getStatus());
    }

    @Test
    void extendTime_Success() {
        contractAgreement.setStatus("ACTIVE");
        contractAgreement.setIsExtended(false);
        LocalDate originalEndDate = contractAgreement.getEndDate();

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);

        contractAgreementService.extendTime(1, 1);

        verify(contractAgreementRepository, times(1)).save(contractAgreement);
        assertTrue(contractAgreement.getIsExtended());
        assertTrue(contractAgreement.getEndDate().isAfter(originalEndDate));
    }

    @Test
    void extendTime_ThrowsException_AlreadyExtended() {
        contractAgreement.setStatus("ACTIVE");
        contractAgreement.setIsExtended(true);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(contractAgreementRepository.findContractAgreementById(1)).thenReturn(contractAgreement);

        ApiException exception = assertThrows(ApiException.class, () -> {
            contractAgreementService.extendTime(1, 1);
        });

        assertEquals("contract already was extended", exception.getMessage());
    }

    @Test
    void holdTokens_Success() {
        Double initialBalance = requesterCredit.getBalance();
        Double amount = 2000.0;

        CreditTransaction result = contractAgreementService.holdTokens(requesterCompany, providerCompany, amount);

        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals("PENDING", result.getStatus());
        assertEquals(initialBalance - amount, requesterCredit.getBalance());
    }

    @Test
    void holdTokens_ThrowsException_InsufficientCredit() {
        requesterCredit.setBalance(100.0);

        ApiException exception = assertThrows(ApiException.class, () -> {
            contractAgreementService.holdTokens(requesterCompany, providerCompany, 5000.0);
        });

        assertEquals("not enough credit", exception.getMessage());
    }

    @Test
    void releaseCredit_Success() {
        Double initialBalance = requesterCredit.getBalance();
        creditTransaction.setStatus("PENDING");

        contractAgreementService.releaseCredit(creditTransaction);

        assertEquals(initialBalance + creditTransaction.getAmount(), requesterCredit.getBalance());
        assertEquals("CANCELLED", creditTransaction.getStatus());
    }

    @Test
    void transferCredit_Success() {
        Double initialBalance = providerCredit.getBalance();
        creditTransaction.setStatus("PENDING");

        contractAgreementService.transferCredit(creditTransaction);

        assertEquals(initialBalance + creditTransaction.getAmount(), providerCredit.getBalance());
        assertEquals("ACCEPTED", creditTransaction.getStatus());
    }

// ==================== COMPLEX SCENARIO TESTS ====================

    @Test
    void complexScenario_MultipleCompaniesAndRequests() {
        // Setup multiple companies and requests
        CompanyProfile company3 = new CompanyProfile();
        company3.setId(3);
        company3.setName("Company 3");
        company3.setServiceRequest(new HashSet<>());

        ServiceRequest request2 = new ServiceRequest();
        request2.setId(2);
        request2.setStatus("OPEN");
        request2.setExchangeType("BARTER");
        request2.setCompanyProfile(company3);
        request2.setCategory(category);
        request2.setServiceBid(new HashSet<>());

        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest, request2));

        var result = serviceRequestService.getServiceRequests();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void complexScenario_BidRejection_ThenNewBid() {
        // First bid rejected
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(requesterCompany);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        serviceBidService.rejectServiceBid(1, 1, "Price too high");
        assertEquals("REJECTED", serviceBid.getStatus());

        // New bid from same provider with different price
        ServiceBid newBid = new ServiceBid();
        newBid.setId(2);
        newBid.setStatus("PENDING");
        newBid.setCompanyProfile(providerCompany);
        newBid.setServiceRequest(serviceRequest);
        newBid.setTokenAmount(4500.0);

        ServiceBidDTOIn newBidDTO = new ServiceBidDTOIn(null, "Revised proposal", null, "Same deliverables", 180.0, LocalDate.now().plusDays(5).toString(), LocalDate.now().plusDays(30).toString(), "TOKENS", 4500.0);

        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(providerCompany);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceBidService.createBid(2, 1, newBidDTO);

        verify(serviceBidRepository, times(2)).save(any(ServiceBid.class));
    }
}