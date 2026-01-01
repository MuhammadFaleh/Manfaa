package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.TicketDTOIn;
import com.v1.manfaa.DTO.In.TicketResolveDTOIn;
import com.v1.manfaa.DTO.Out.TicketDTOOut;
import com.v1.manfaa.Model.*;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ContractAgreementRepository;
import com.v1.manfaa.Repository.TicketRepository;
import com.v1.manfaa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final ContractAgreementRepository contractAgreementRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<TicketDTOOut> getAllTickets() {
        return convertToDtoOut(ticketRepository.findAll());
    }

    public void addTicketContract(Integer companyId, Integer contractId, TicketDTOIn dto) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);
        if(company == null) {
            throw new  ApiException("Company not found");
        }

        ContractAgreement contract = contractAgreementRepository.findById(contractId)
                .orElseThrow(() -> new ApiException("Contract not found"));

        if (!contract.getRequesterCompanyProfile().equals(company) &&
                !contract.getProviderCompanyProfile().equals(company)) {
            throw new ApiException("Company is not a part of this contract");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCategory("CONTRACT");
        ticket.setPriority("HIGH");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        ticket.setCompanyProfile(company);
        ticket.setContractAgreement(contract);
        ticket.setResolvedBy(null);

        ticketRepository.save(ticket);
    }

    public void updateTicket(Integer ticketId, Integer companyId, TicketDTOIn dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException("Ticket not found"));

        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);
        if(company == null) {
            throw new  ApiException("Company not found");
        }

        if (!ticket.getCompanyProfile().equals(company)) {
            throw new ApiException("Company does not own this ticket");
        }

        if ("RESOLVED".equals(ticket.getStatus()) || "CLOSED".equals(ticket.getStatus())) {
            throw new ApiException("Cannot update a resolved or closed ticket");
        }

        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());

        ticketRepository.save(ticket);
    }

    public void deleteTicket(Integer ticketId, Integer companyId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException("Ticket not found"));

        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);
        if(company == null) {
            throw new  ApiException("Company not found");
        }

        if (!ticket.getCompanyProfile().equals(company)) {
            throw new ApiException("Company does not own this ticket");
        }

        if (!"OPEN".equals(ticket.getStatus())) {
            throw new ApiException("Can only delete tickets with 'OPEN' status");
        }

        ticketRepository.delete(ticket);
    }

    public List<TicketDTOOut> convertToDtoOut(List<Ticket> tickets) {
        List<TicketDTOOut> ticketDTOOuts = new ArrayList<>();
        for(Ticket ticket : tickets){
            ticketDTOOuts.add(new TicketDTOOut(
                    ticket.getId(),
                    ticket.getContractAgreement() != null ? ticket.getContractAgreement().getId() : null,
                    ticket.getCompanyProfile().getName(),
                    ticket.getTitle(),
                    ticket.getBody(),
                    ticket.getCategory(),
                    ticket.getPriority(),
                    ticket.getCreatedAt(),
                    ticket.getResolvedAt(),
                    ticket.getStatus()
            ));
        }

        return ticketDTOOuts;
    }

    public void addTicketSuggestion(Integer companyId, TicketDTOIn dto) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);
        if(company == null) {
            throw new  ApiException("Company not found");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCategory("SUGGESTION");
        if(company.getIsSubscriber()){
            ticket.setPriority("MEDIUM");
        } else {ticket.setPriority("LOW");}
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        ticket.setCompanyProfile(company);
        ticket.setContractAgreement(null);
        ticket.setResolvedBy(null);

        ticketRepository.save(ticket);
    }

    //'SUBSCRIPTION' or category='PLATFORM' 'OPEN' or status = 'RESOLVED' or status = 'CLOSED'
    public void addTicketSubscription(Integer companyId, TicketDTOIn dto) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);
        if(company == null) {
            throw new  ApiException("Company not found");
        }
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCategory("SUBSCRIPTION");
        if(company.getIsSubscriber()){
            ticket.setPriority("HIGH");
        } else {ticket.setPriority("MEDIUM");}
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        ticket.setCompanyProfile(company);
        ticket.setContractAgreement(null);
        ticket.setResolvedBy(null);

        ticketRepository.save(ticket);
    }

    public void addTicketPlatform(Integer companyId, TicketDTOIn dto) {
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyId);
        if(company == null) {
            throw new  ApiException("Company not found");
        }
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCategory("PLATFORM");
        if(company.getIsSubscriber()){
            ticket.setPriority("HIGH");
        } else {ticket.setPriority("MEDIUM");}
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        ticket.setCompanyProfile(company);
        ticket.setContractAgreement(null);
        ticket.setResolvedBy(null);

        ticketRepository.save(ticket);
    }

    public void resolveTicket(Integer adminId, TicketResolveDTOIn dto){//update admin
        User user = userRepository.findUserById(adminId);
        Ticket ticket = ticketRepository.findTicketById(dto.getTicketId());
        if(user == null) {
            throw new  ApiException("user not found");
        }

        if(ticket == null){
            throw new ApiException("ticket not found");
        }

        if ("RESOLVED".equals(ticket.getStatus()) || "CLOSED".equals(ticket.getStatus())) {
            throw new ApiException("Cannot update a resolved or closed ticket");
        }

        if("CONTRACT".equalsIgnoreCase(ticket.getCategory())){
            ContractAgreement contractAgreement= ticket.getContractAgreement();
            contractAgreement.setStatus("DISPUTED");
            contractAgreementRepository.save(contractAgreement);
        }

        ticket.setResolvedBy(adminId);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setStatus("RESOLVED");
        String recipientEmail = ticket.getCompanyProfile().getUser().getEmail();
        String subject = "Support Ticket #" + ticket.getId() + " - " + ticket.getCategory();

        String message = "Dear " + ticket.getCompanyProfile().getName() + ",\n\n"
                + "We have received and reviewed your support ticket.\n\n"
                + "Ticket Details:\n"
                + "Ticket Number: #" + ticket.getId() + "\n"
                + "Category: " + ticket.getCategory() + "\n\n"
                + "Response from our support team:\n"
                + "-----------------------------------\n"
                + dto.getBody() + "\n"
                + "-----------------------------------\n\n"
                + "If you have any further questions or need additional assistance, please don't hesitate to contact us.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);
        ticketRepository.save(ticket);
    }


    public void rejectTicket(Integer adminId, TicketResolveDTOIn dto){//update admin
        User user = userRepository.findUserById(adminId);
        Ticket ticket = ticketRepository.findTicketById(dto.getTicketId());
        if(user == null) {
            throw new  ApiException("user not found");
        }

        if(ticket == null){
            throw new ApiException("ticket not found");
        }

        if ("RESOLVED".equals(ticket.getStatus()) || "CLOSED".equals(ticket.getStatus())) {
            throw new ApiException("Cannot update a resolved or closed ticket");
        }

        ticket.setResolvedBy(adminId);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setStatus("CLOSED");
        String recipientEmail = ticket.getCompanyProfile().getUser().getEmail();
        String subject = "Support Ticket #" + ticket.getId() + " - " + ticket.getCategory();

        String message = "Dear " + ticket.getCompanyProfile().getName() + ",\n\n"
                + "We have received and reviewed your support ticket.\n\n"
                + "Ticket Details:\n"
                + "Ticket Number: #" + ticket.getId() + "\n"
                + "Category: " + ticket.getCategory() + "\n\n"
                + "Response from our support team:\n"
                + "-----------------------------------\n"
                + dto.getBody() + "\n"
                + "-----------------------------------\n\n"
                + "If you have any further questions or need additional assistance, please don't hesitate to contact us.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);
        ticketRepository.save(ticket);
    }

    public List<TicketDTOOut> showMyTickets(Integer userId){
        return convertToDtoOut(ticketRepository.findByCompanyProfileId(userId));
    }

    public List<TicketDTOOut> getTicketsBySubscriberStatus(Boolean isSubscriber) {
        return convertToDtoOut(ticketRepository.findByCompanyProfileIsSubscriber(isSubscriber));
    }

    public List<TicketDTOOut> getTicketsByCategoryAndSubscriberStatus(String category, Boolean isSubscriber) {
        return convertToDtoOut(ticketRepository.findByCategoryAndCompanyProfileIsSubscriber(category, isSubscriber));
    }

    public List<TicketDTOOut> getTicketsByPriorityAndSubscriberStatus(String priority, Boolean isSubscriber) {
        return convertToDtoOut(ticketRepository.findByPriorityAndCompanyProfileIsSubscriber(priority, isSubscriber));
    }

    public List<TicketDTOOut> getTicketsByCategoryAndPriorityAndSubscriberStatus(String category, String priority, Boolean isSubscriber) {
        return convertToDtoOut(ticketRepository.findByCategoryAndPriorityAndCompanyProfileIsSubscriber(category, priority, isSubscriber));
    }

    public List<TicketDTOOut> getMyTicketsByStatus(Integer userId, String status) {
        return convertToDtoOut(ticketRepository.findByStatusAndCompanyProfileId(status, userId));
    }

}
