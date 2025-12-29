package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ServiceBidDTOIn;
import com.v1.manfaa.DTO.Out.ServiceBidDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ContractAgreement;
import com.v1.manfaa.Model.ServiceBid;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Repository.CategoryRepository;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ServiceBidRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceBidService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceBidRepository serviceBidRepository;
    private final EmailService emailService;

    public List<ServiceBidDTOOut> getAllBids(){
        List<ServiceBidDTOOut> dtoOuts = new ArrayList<>();
        for(ServiceBid bid : serviceBidRepository.findAll()){
            dtoOuts.add(convertToDTO(bid));
        }

        return dtoOuts;
    }

    public void createBid(Integer company_id, Integer request_id, ServiceBidDTOIn dtoIn){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(company_id);
        ServiceRequest serviceRequest = serviceRequestRepository.findServiceRequestById(request_id);

        if(companyProfile == null){
            throw new ApiException("company not found");
        }
        if(serviceRequest == null){
            throw new ApiException("request not found");
        }

        if(!serviceRequest.getStatus().equalsIgnoreCase("OPEN")){
            throw new ApiException("service request is closed or canceled and can't take any new bids");
        }

        if(dtoIn.getProposedStartDate().isAfter(dtoIn.getProposedEndDate()) ||
                ChronoUnit.HOURS.between(dtoIn.getProposedStartDate(), dtoIn.getProposedEndDate()) <
                        dtoIn.getEstimatedHours()){
            throw new ApiException("wrong dates expected hours and date don't make sense");
        }

        ServiceBid bid = convertToEntity(dtoIn);

        if(serviceRequest.getExchangeType().equalsIgnoreCase("EITHER")){
            bid.setPaymentMethod(dtoIn.getExchangeType());
        }
        if(serviceRequest.getExchangeType().equalsIgnoreCase(dtoIn.getExchangeType())){
            bid.setPaymentMethod(serviceRequest.getExchangeType());
        }else{
            throw new ApiException("exchange type not the same as the request");
        }

        bid.setCreatedAt(LocalDateTime.now());
        bid.setStatus("PENDING");
        bid.setServiceRequest(serviceRequest);
        bid.setCompanyProfile(companyProfile);
        serviceRequest.getServiceBid().add(bid);
        companyProfile.getServiceBid().add(bid);
        serviceBidRepository.save(bid);
        companyProfileRepository.save(companyProfile);
        serviceRequestRepository.save(serviceRequest);
    }

    public void updateBid(ServiceBidDTOIn dtoIn, Integer id, Integer bid_id){
        ServiceBid bid  = serviceBidRepository.findServiceBidById(bid_id);

        if(bid == null || !bid.getCompanyProfile().getId().equals(id)){
            throw new ApiException("bid not found");
        }
        if(!bid.getStatus().equalsIgnoreCase("PENDING")){
            throw new ApiException("bid is already checked and can't be updated");
        }

        bid.setEstimatedHours(dtoIn.getEstimatedHours());
        bid.setDeliverables(dtoIn.getDeliverables());
        bid.setDescription(dtoIn.getDescription());
        bid.setTokenAmount(dtoIn.getTokenAmount());
        serviceBidRepository.save(bid);
    }

    public void deleteBid(Integer id, Integer bid_id){
        ServiceBid bid  = serviceBidRepository.findServiceBidById(bid_id);

        if(bid == null || !bid.getCompanyProfile().getId().equals(id)){
            throw new ApiException("bid not found");
        }
        if(!bid.getStatus().equalsIgnoreCase("PENDING")){
            throw new ApiException("bid is already checked and can't be deleted");
        }
        CompanyProfile companyProfile = bid.getCompanyProfile();
        ServiceRequest serviceRequest = bid.getServiceRequest();

        companyProfile.getServiceBid().remove(bid);
        serviceRequest.getServiceBid().remove(bid);
        companyProfileRepository.save(companyProfile);
        serviceRequestRepository.save(serviceRequest);
        bid.setServiceRequest(null);
        bid.setCompanyProfile(null);
        serviceBidRepository.delete(bid);
    }

    public void acceptServiceBid(Integer serviceBidId, Integer userId){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);
        ServiceBid serviceBid = serviceBidRepository.findServiceBidById(serviceBidId);

        if(companyProfile == null || serviceBid == null ||
                !serviceBid.getServiceRequest().getCompanyProfile().getId().equals(userId)){
            throw new ApiException("service bid not found");
        }
        ServiceRequest serviceRequest = serviceBid.getServiceRequest();
        if(!serviceRequest.getStatus().equalsIgnoreCase("OPEN")
                || !serviceBid.getStatus().equalsIgnoreCase("PENDING")){
            throw new ApiException("service bid or request is already closed");
        }



        serviceRequest.setStatus("CLOSED");
        serviceBid.setStatus("ACCEPTED");
        serviceRequestRepository.save(serviceRequest);
        serviceBidRepository.save(serviceBid);



    }

    public void rejectServiceBid(Integer serviceBidId, Integer userId, String notes){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(userId);
        ServiceBid serviceBid = serviceBidRepository.findServiceBidById(serviceBidId);

        if(companyProfile == null || serviceBid == null ||
                !serviceBid.getServiceRequest().getCompanyProfile().getId().equals(userId)){
            throw new ApiException("service bid not found");
        }
        ServiceRequest serviceRequest = serviceBid.getServiceRequest();
        if(!serviceRequest.getStatus().equalsIgnoreCase("OPEN")
                || !serviceBid.getStatus().equalsIgnoreCase("PENDING")){
            throw new ApiException("service bid or request is already closed");
        }

        // send an email

        String recipientEmail = serviceBid.getCompanyProfile().getUser().getEmail();
        String subject = "Service Bid Rejected";

        String message = "Dear " + serviceBid.getCompanyProfile().getName() + ",\n\n"
                + "We would like to inform you that your service bid for the request titled \""
                + serviceRequest.getTitle() + "\" has been rejected by the service requester.\n\n"
                +"Notes given by the service requester: " + notes
                + "Thank you for your interest and for submitting your bid. "
                + "You are welcome to apply for other service requests available on the platform.\n\n"
                + "Kind regards,\n"
                + "Support Team";

        emailService.sendEmail(recipientEmail, subject, message);

        serviceBid.setNotes(notes);
        serviceBid.setStatus("REJECTED");
        serviceBidRepository.save(serviceBid);



    }


    public ServiceBid convertToEntity(ServiceBidDTOIn dtoIn){
        return new ServiceBid(null,dtoIn.getDescription(),null,dtoIn.getDeliverables(),dtoIn.getEstimatedHours(),
                dtoIn.getProposedStartDate(),dtoIn.getProposedEndDate(),null,dtoIn.getTokenAmount(),null,
                null,null,null,null);
    }
    public ServiceBidDTOOut convertToDTO(ServiceBid bid){
        return new ServiceBidDTOOut(bid.getId(),bid.getDescription(),bid.getNotes(),bid.getDeliverables(),
                bid.getEstimatedHours(),bid.getProposedStartDate(),bid.getProposedEndDate(),bid.getPaymentMethod(),
                bid.getTokenAmount());
    }
}
