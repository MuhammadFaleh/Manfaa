package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.TicketDTOIn;
import com.v1.manfaa.DTO.Out.TicketDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ContractAgreementRepository;
import com.v1.manfaa.Repository.TicketRepository;
import com.v1.manfaa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final ContractAgreementRepository contractAgreementRepository;
    private final UserRepository userRepository;

    public List<TicketDTOOut> getAllTickets(Integer adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException("Admin not found"));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new ApiException("User is not an admin");
        }

        return convertToDtoOut(ticketRepository.findAll());
    }

    public void addTicket(Integer companyId, Integer contractId, TicketDTOIn dto) {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ApiException("Company not found"));

        ContractAgreement contract = contractAgreementRepository.findById(contractId)
                .orElseThrow(() -> new ApiException("Contract not found"));

        if (!contract.getRequesterCompanyProfile().equals(company) &&
                !contract.getProviderCompanyProfile().equals(company)) {
            throw new ApiException("Company is not a part of this contract");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCategory(dto.getCategory());
        ticket.setPriority("HIGH"); //Todo: Must be decided via AI
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        ticket.setCompanyProfile(company);
        ticket.setContractAgreement(contract);

        ticketRepository.save(ticket);
    }

    public void updateTicket(Integer ticketId, Integer companyId, TicketDTOIn dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException("Ticket not found"));

        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ApiException("Company not found"));

        if (!ticket.getCompanyProfile().equals(company)) {
            throw new ApiException("Company does not own this ticket");
        }

        if ("RESOLVED".equals(ticket.getStatus()) || "CLOSED".equals(ticket.getStatus())) {
            throw new ApiException("Cannot update a resolved or closed ticket");
        }

        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCategory(dto.getCategory());

        ticketRepository.save(ticket);
    }

    public void deleteTicket(Integer ticketId, Integer companyId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException("Ticket not found"));

        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ApiException("Company not found"));

        if (!ticket.getCompanyProfile().equals(company)) {
            throw new ApiException("Company does not own this ticket");
        }

        if (!"OPEN".equals(ticket.getStatus())) {
            throw new ApiException("Can only delete tickets with 'OPEN' status");
        }

        ticketRepository.delete(ticket);
    }

    public List<TicketDTOOut> convertToDtoOut(List<Ticket> tickets) {
        return tickets.stream()
                .map(
                        ticket-> new TicketDTOOut(
                        ticket.getId(),
                        ticket.getContractAgreement().getId(),
                        ticket.getCompanyProfile().getName(),
                        ticket.getTitle(),
                        ticket.getBody(),
                        ticket.getCategory(),
                        ticket.getPriority(),
                        ticket.getCreatedAt(),
                        ticket.getResolvedAt(),
                        ticket.getStatus()
                ))
                .toList();
    }
}
