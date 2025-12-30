package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ServiceBidDTOIn;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import com.v1.manfaa.Service.EmailService;
import com.v1.manfaa.Service.ServiceBidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBidServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private ServiceBidRepository serviceBidRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ServiceBidService serviceBidService;

    private CompanyProfile companyProfile;
    private ServiceRequest serviceRequest;
    private ServiceBidDTOIn bidDTOIn;
    private ServiceBid serviceBid;

    @BeforeEach
    void setUp() {
        companyProfile = new CompanyProfile();
        companyProfile.setId(1);
        companyProfile.setName("Test Company");
        companyProfile.setServiceBid(new HashSet<>());

        User user = new User();
        user.setEmail("test@example.com");
        companyProfile.setUser(user);

        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1);
        serviceRequest.setTitle("Test Request");
        serviceRequest.setStatus("OPEN");
        serviceRequest.setExchangeType("TOKENS");
        serviceRequest.setCompanyProfile(companyProfile);
        serviceRequest.setServiceBid(new HashSet<>());

        bidDTOIn = new ServiceBidDTOIn(null,"Test Bid","Deliverable 1, Deliverable 2","blah blah",40.0,LocalDate.now().plusDays(1).toString(),LocalDate.now().plusDays(10).toString(),"TOKENS",1000.0);

        serviceBid = new ServiceBid();
        serviceBid.setId(1);
        serviceBid.setStatus("PENDING");
        serviceBid.setCompanyProfile(companyProfile);
        serviceBid.setServiceRequest(serviceRequest);
    }

    // CREATE BID TESTS

    @Test
    void createBid_Success_WithTokenExchange() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceBidService.createBid(1, 1, bidDTOIn);

        verify(serviceBidRepository, times(1)).save(any(ServiceBid.class));
        verify(companyProfileRepository, times(1)).save(companyProfile);
        verify(serviceRequestRepository, times(1)).save(serviceRequest);
    }

    @Test
    void createBid_Success_WithBarterExchange() {
        serviceRequest.setExchangeType("BARTER");
        bidDTOIn.setExchangeType("BARTER");
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceBidService.createBid(1, 1, bidDTOIn);

        verify(serviceBidRepository, times(1)).save(any(ServiceBid.class));
        verify(companyProfileRepository, times(1)).save(companyProfile);
        verify(serviceRequestRepository, times(1)).save(serviceRequest);
    }


    @Test
    void createBid_Success_WithEitherExchangeType() {
        serviceRequest.setExchangeType("EITHER");
        bidDTOIn.setExchangeType("BARTER");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        serviceBidService.createBid(1, 1, bidDTOIn);

        verify(serviceBidRepository, times(1)).save(any(ServiceBid.class));
    }

    @Test
    void createBid_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(1, 1, bidDTOIn);
        });

        assertEquals("company not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void createBid_ThrowsException_RequestNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(1, 1, bidDTOIn);
        });

        assertEquals("request not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void createBid_ThrowsException_RequestNotOpen() {
        serviceRequest.setStatus("CLOSED");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(1, 1, bidDTOIn);
        });

        assertEquals("service request is closed or canceled and can't take any new bids", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void createBid_ThrowsException_InvalidDates_StartAfterEnd() {
        bidDTOIn.setProposedStartDate(LocalDate.now().plusDays(10).toString());
        bidDTOIn.setProposedEndDate(LocalDate.now().plusDays(1).toString());

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(1, 1, bidDTOIn);
        });

        assertEquals("wrong dates expected hours and date don't make sense", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void createBid_ThrowsException_EstimatedHoursExceedDateRange() {
        bidDTOIn.setProposedStartDate(LocalDate.now().plusDays(1).toString());
        bidDTOIn.setProposedEndDate(LocalDate.now().plusDays(2).toString());
        bidDTOIn.setEstimatedHours(1000.0); // More hours than the date range allows

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(1, 1, bidDTOIn);
        });

        assertEquals("wrong dates expected hours and date don't make sense", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void createBid_ThrowsException_ExchangeTypeMismatch() {
        serviceRequest.setExchangeType("TOKENS");
        bidDTOIn.setExchangeType("BARTER");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceRequestRepository.findServiceRequestById(1)).thenReturn(serviceRequest);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.createBid(1, 1, bidDTOIn);
        });

        assertEquals("exchange type not the same as the request", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    // UPDATE BID TESTS

    @Test
    void updateBid_Success() {
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ServiceBidDTOIn updateDTO = new ServiceBidDTOIn(null,"Updated Description",null,"Updated Deliverables",50.0,LocalDate.now().toString(),LocalDate.now().plusDays(10).toString(),"TOKENS",1500.0);

        serviceBidService.updateBid(updateDTO, 1, 1);

        verify(serviceBidRepository, times(1)).save(serviceBid);
        assertEquals("Updated Description", serviceBid.getDescription());
        assertEquals(50, serviceBid.getEstimatedHours());
    }

    @Test
    void updateBid_ThrowsException_BidNotFound() {
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.updateBid(bidDTOIn, 1, 1);
        });

        assertEquals("bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void updateBid_ThrowsException_UnauthorizedCompany() {
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.updateBid(bidDTOIn, 999, 1);
        });

        assertEquals("bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void updateBid_ThrowsException_BidAlreadyAccepted() {
        serviceBid.setStatus("ACCEPTED");
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.updateBid(bidDTOIn, 1, 1);
        });

        assertEquals("bid is already checked and can't be updated", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void updateBid_ThrowsException_BidAlreadyRejected() {
        serviceBid.setStatus("REJECTED");
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.updateBid(bidDTOIn, 1, 1);
        });

        assertEquals("bid is already checked and can't be updated", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    // DELETE BID TESTS

    @Test
    void deleteBid_Success() {
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        serviceBidService.deleteBid(1, 1);

        verify(companyProfileRepository, times(1)).save(companyProfile);
        verify(serviceRequestRepository, times(1)).save(serviceRequest);
        verify(serviceBidRepository, times(1)).delete(serviceBid);
    }

    @Test
    void deleteBid_ThrowsException_BidNotFound() {
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.deleteBid(1, 1);
        });

        assertEquals("bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).delete(any());
    }

    @Test
    void deleteBid_ThrowsException_UnauthorizedCompany() {
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.deleteBid(999, 1);
        });

        assertEquals("bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).delete(any());
    }

    @Test
    void deleteBid_ThrowsException_BidAlreadyChecked() {
        serviceBid.setStatus("ACCEPTED");
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.deleteBid(1, 1);
        });

        assertEquals("bid is already checked and can't be deleted", exception.getMessage());
        verify(serviceBidRepository, never()).delete(any());
    }

    // ACCEPT BID TESTS

    @Test
    void acceptServiceBid_Success() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        serviceBidService.acceptServiceBid(1, 1);

        verify(serviceRequestRepository, times(1)).save(serviceRequest);
        verify(serviceBidRepository, times(1)).save(serviceBid);
        assertEquals("CLOSED", serviceRequest.getStatus());
        assertEquals("ACCEPTED", serviceBid.getStatus());
    }

    @Test
    void acceptServiceBid_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.acceptServiceBid(1, 1);
        });

        assertEquals("service bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void acceptServiceBid_ThrowsException_BidNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.acceptServiceBid(1, 1);
        });

        assertEquals("service bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void acceptServiceBid_ThrowsException_UnauthorizedUser() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);
        serviceRequest.setCompanyProfile(differentCompany);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.acceptServiceBid(1, 1);
        });

        assertEquals("service bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void acceptServiceBid_ThrowsException_RequestNotOpen() {
        serviceRequest.setStatus("CLOSED");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.acceptServiceBid(1, 1);
        });

        assertEquals("service bid or request is already closed", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    @Test
    void acceptServiceBid_ThrowsException_BidNotPending() {
        serviceBid.setStatus("REJECTED");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.acceptServiceBid(1, 1);
        });

        assertEquals("service bid or request is already closed", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
    }

    // REJECT BID TESTS

    @Test
    void rejectServiceBid_Success() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        serviceBidService.rejectServiceBid(1, 1, "Not suitable for our requirements");

        verify(serviceBidRepository, times(1)).save(serviceBid);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        assertEquals("REJECTED", serviceBid.getStatus());
        assertEquals("Not suitable for our requirements", serviceBid.getNotes());
    }

    @Test
    void rejectServiceBid_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.rejectServiceBid(1, 1, "Notes");
        });

        assertEquals("service bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void rejectServiceBid_ThrowsException_BidNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.rejectServiceBid(1, 1, "Notes");
        });

        assertEquals("service bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void rejectServiceBid_ThrowsException_UnauthorizedUser() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);
        serviceRequest.setCompanyProfile(differentCompany);

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.rejectServiceBid(1, 1, "Notes");
        });

        assertEquals("service bid not found", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void rejectServiceBid_ThrowsException_RequestNotOpen() {
        serviceRequest.setStatus("CLOSED");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.rejectServiceBid(1, 1, "Notes");
        });

        assertEquals("service bid or request is already closed", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void rejectServiceBid_ThrowsException_BidAlreadyAccepted() {
        serviceBid.setStatus("ACCEPTED");

        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(serviceBidRepository.findServiceBidById(1)).thenReturn(serviceBid);

        ApiException exception = assertThrows(ApiException.class, () -> {
            serviceBidService.rejectServiceBid(1, 1, "Notes");
        });

        assertEquals("service bid or request is already closed", exception.getMessage());
        verify(serviceBidRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
