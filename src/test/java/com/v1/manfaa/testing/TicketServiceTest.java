package com.v1.manfaa.testing;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.TicketDTOIn;
import com.v1.manfaa.DTO.In.TicketResolveDTOIn;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.*;
import com.v1.manfaa.Service.EmailService;
import com.v1.manfaa.Service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private ContractAgreementRepository contractAgreementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TicketService ticketService;

    private CompanyProfile companyProfile;
    private CompanyProfile subscriberCompany;
    private ContractAgreement contractAgreement;
    private Ticket ticket;
    private TicketDTOIn ticketDTOIn;
    private User admin;
    private User regularUser;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setId(1);
        regularUser.setEmail("user@example.com");

        companyProfile = new CompanyProfile();
        companyProfile.setId(1);
        companyProfile.setName("Test Company");
        companyProfile.setIsSubscriber(false);
        companyProfile.setUser(regularUser);

        subscriberCompany = new CompanyProfile();
        subscriberCompany.setId(2);
        subscriberCompany.setName("Subscriber Company");
        subscriberCompany.setIsSubscriber(true);
        subscriberCompany.setUser(regularUser);

        contractAgreement = new ContractAgreement();
        contractAgreement.setId(1);
        contractAgreement.setRequesterCompanyProfile(companyProfile);
        contractAgreement.setProviderCompanyProfile(subscriberCompany);

        ticket = new Ticket();
        ticket.setId(1);
        ticket.setTitle("Test Ticket");
        ticket.setBody("Test ticket body");
        ticket.setCategory("CONTRACT");
        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setCompanyProfile(companyProfile);
        ticket.setContractAgreement(contractAgreement);

        ticketDTOIn = new TicketDTOIn("New Ticket","Ticket description");

        admin = new User();
        admin.setId(10);
        admin.setRole("ADMIN");
        admin.setEmail("admin@example.com");
    }

    // ADD TICKET CONTRACT TESTS

    @Test
    void addTicketContract_Success() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(contractAgreementRepository.findById(1)).thenReturn(Optional.of(contractAgreement));

        ticketService.addTicketContract(1, 1, ticketDTOIn);

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void addTicketContract_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.addTicketContract(1, 1, ticketDTOIn);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void addTicketContract_ThrowsException_ContractNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);
        when(contractAgreementRepository.findById(1)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.addTicketContract(1, 1, ticketDTOIn);
        });

        assertEquals("Contract not found", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void addTicketContract_ThrowsException_CompanyNotPartOfContract() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);

        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(differentCompany);
        when(contractAgreementRepository.findById(1)).thenReturn(Optional.of(contractAgreement));

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.addTicketContract(999, 1, ticketDTOIn);
        });

        assertEquals("Company is not a part of this contract", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    // ADD TICKET SUGGESTION TESTS

    @Test
    void addTicketSuggestion_Success_NonSubscriber() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ticketService.addTicketSuggestion(1, ticketDTOIn);

        verify(ticketRepository, times(1)).save(argThat(t ->
                t.getCategory().equals("SUGGESTION") &&
                        t.getPriority().equals("LOW") &&
                        t.getContractAgreement() == null
        ));
    }

    @Test
    void addTicketSuggestion_Success_Subscriber() {
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(subscriberCompany);

        ticketService.addTicketSuggestion(2, ticketDTOIn);

        verify(ticketRepository, times(1)).save(argThat(t ->
                t.getCategory().equals("SUGGESTION") &&
                        t.getPriority().equals("MEDIUM")
        ));
    }

    @Test
    void addTicketSuggestion_ThrowsException_CompanyNotFound() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.addTicketSuggestion(1, ticketDTOIn);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    // ADD TICKET SUBSCRIPTION TESTS

    @Test
    void addTicketSubscription_Success_NonSubscriber() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ticketService.addTicketSubscription(1, ticketDTOIn);

        verify(ticketRepository, times(1)).save(argThat(t ->
                t.getCategory().equals("SUBSCRIPTION") &&
                        t.getPriority().equals("MEDIUM")
        ));
    }

    @Test
    void addTicketSubscription_Success_Subscriber() {
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(subscriberCompany);

        ticketService.addTicketSubscription(2, ticketDTOIn);

        verify(ticketRepository, times(1)).save(argThat(t ->
                t.getCategory().equals("SUBSCRIPTION") &&
                        t.getPriority().equals("HIGH")
        ));
    }

    // ADD TICKET PLATFORM TESTS

    @Test
    void addTicketPlatform_Success_NonSubscriber() {
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ticketService.addTicketPlatform(1, ticketDTOIn);

        verify(ticketRepository, times(1)).save(argThat(t ->
                t.getCategory().equals("PLATFORM") &&
                        t.getPriority().equals("MEDIUM")
        ));
    }

    @Test
    void addTicketPlatform_Success_Subscriber() {
        when(companyProfileRepository.findCompanyProfileById(2)).thenReturn(subscriberCompany);

        ticketService.addTicketPlatform(2, ticketDTOIn);

        verify(ticketRepository, times(1)).save(argThat(t ->
                t.getCategory().equals("PLATFORM") &&
                        t.getPriority().equals("HIGH")
        ));
    }

    // UPDATE TICKET TESTS

    @Test
    void updateTicket_Success() {
        TicketDTOIn updateDTO = new TicketDTOIn("Updated Title","Updated Body");

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ticketService.updateTicket(1, 1, updateDTO);

        verify(ticketRepository, times(1)).save(ticket);
        assertEquals("Updated Title", ticket.getTitle());
        assertEquals("Updated Body", ticket.getBody());
    }

    @Test
    void updateTicket_ThrowsException_TicketNotFound() {
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.updateTicket(1, 1, ticketDTOIn);
        });

        assertEquals("Ticket not found", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ThrowsException_CompanyNotFound() {
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.updateTicket(1, 1, ticketDTOIn);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ThrowsException_CompanyDoesNotOwnTicket() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(differentCompany);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.updateTicket(1, 999, ticketDTOIn);
        });

        assertEquals("Company does not own this ticket", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ThrowsException_TicketResolved() {
        ticket.setStatus("RESOLVED");

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.updateTicket(1, 1, ticketDTOIn);
        });

        assertEquals("Cannot update a resolved or closed ticket", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ThrowsException_TicketClosed() {
        ticket.setStatus("CLOSED");

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.updateTicket(1, 1, ticketDTOIn);
        });

        assertEquals("Cannot update a resolved or closed ticket", exception.getMessage());
        verify(ticketRepository, never()).save(any());
    }




    // RESOLVE TICKET TESTS

    @Test
    void resolveTicket_Success() {
        TicketResolveDTOIn resolveDTO = new TicketResolveDTOIn(1,"Issue has been resolved");

        when(userRepository.findUserById(10)).thenReturn(admin);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        ticketService.resolveTicket(10, resolveDTO);

        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        assertEquals("RESOLVED", ticket.getStatus());
        assertEquals(10, ticket.getResolvedBy());
        assertNotNull(ticket.getResolvedAt());
    }

    @Test
    void resolveTicket_ThrowsException_UserNotFound() {
        TicketResolveDTOIn resolveDTO = new TicketResolveDTOIn(1,"b;aj");
        resolveDTO.setTicketId(1);

        when(userRepository.findUserById(10)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.resolveTicket(10, resolveDTO);
        });

        assertEquals("user not found", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resolveTicket_ThrowsException_TicketNotFound() {
        TicketResolveDTOIn resolveDTO = new TicketResolveDTOIn(1,"v");
        resolveDTO.setTicketId(1);

        when(userRepository.findUserById(10)).thenReturn(admin);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.resolveTicket(10, resolveDTO);
        });

        assertEquals("ticket not found", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resolveTicket_ThrowsException_TicketAlreadyResolved() {
        ticket.setStatus("RESOLVED");

        TicketResolveDTOIn resolveDTO = new TicketResolveDTOIn(1,"s");
        resolveDTO.setTicketId(1);

        when(userRepository.findUserById(10)).thenReturn(admin);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.resolveTicket(10, resolveDTO);
        });

        assertEquals("Cannot update a resolved or closed ticket", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resolveTicket_ThrowsException_TicketAlreadyClosed() {
        ticket.setStatus("CLOSED");

        TicketResolveDTOIn resolveDTO = new TicketResolveDTOIn(1,"1");
        resolveDTO.setTicketId(1);

        when(userRepository.findUserById(10)).thenReturn(admin);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.resolveTicket(10, resolveDTO);
        });

        assertEquals("Cannot update a resolved or closed ticket", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // REJECT TICKET TESTS

    @Test
    void rejectTicket_Success() {
        TicketResolveDTOIn rejectDTO = new TicketResolveDTOIn(1,"We cannot process this request");
        rejectDTO.setTicketId(1);
        rejectDTO.setBody("We cannot process this request");

        when(userRepository.findUserById(10)).thenReturn(admin);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        ticketService.rejectTicket(10, rejectDTO);

        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        assertEquals("CLOSED", ticket.getStatus());
        assertEquals(10, ticket.getResolvedBy());
        assertNotNull(ticket.getResolvedAt());
    }

    @Test
    void rejectTicket_ThrowsException_UserNotFound() {
        TicketResolveDTOIn rejectDTO = new TicketResolveDTOIn(1,"1");
        rejectDTO.setTicketId(1);

        when(userRepository.findUserById(10)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.rejectTicket(10, rejectDTO);
        });

        assertEquals("user not found", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void rejectTicket_ThrowsException_TicketAlreadyResolved() {
        TicketResolveDTOIn rejectDTO = new TicketResolveDTOIn(2,"1");
        ticket = new Ticket();
        ticket.setId(2);
        ticket.setTitle("Test Ticket");
        ticket.setBody("Test ticket body");
        ticket.setCategory("CONTRACT");
        ticket.setPriority("HIGH");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setCompanyProfile(companyProfile);
        ticket.setContractAgreement(contractAgreement);
        ticket.setStatus("RESOLVED");
        ticketRepository.save(ticket);


        when(userRepository.findUserById(10)).thenReturn(admin);
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.rejectTicket(10, rejectDTO);
        });

        assertEquals("Cannot update a resolved or closed ticket", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deleteTicket_ThrowsException_TicketNotOpen() {
        ticket.setStatus("RESOLVED");

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.deleteTicket(1, 1);
        });

        assertEquals("Can only delete tickets with 'OPEN' status", exception.getMessage());
        verify(ticketRepository, never()).delete(any());
    }

    // DELETE TICKET TESTS

    @Test
    void deleteTicket_Success() {
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(companyProfile);

        ticketService.deleteTicket(1, 1);

        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void deleteTicket_ThrowsException_TicketNotFound() {
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.deleteTicket(1, 1);
        });

        assertEquals("Ticket not found", exception.getMessage());
        verify(ticketRepository, never()).delete(any());
    }

    @Test
    void deleteTicket_ThrowsException_CompanyNotFound() {
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(1)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.deleteTicket(1, 1);
        });

        assertEquals("Company not found", exception.getMessage());
        verify(ticketRepository, never()).delete(any());
    }

    @Test
    void deleteTicket_ThrowsException_CompanyDoesNotOwnTicket() {
        CompanyProfile differentCompany = new CompanyProfile();
        differentCompany.setId(999);

        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(companyProfileRepository.findCompanyProfileById(999)).thenReturn(differentCompany);

        ApiException exception = assertThrows(ApiException.class, () -> {
            ticketService.deleteTicket(1, 999);
        });

        assertEquals("Company does not own this ticket", exception.getMessage());
        verify(ticketRepository, never()).delete(any());
    }

}
