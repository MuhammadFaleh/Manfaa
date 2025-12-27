package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ServiceBidDTOIn;
import com.v1.manfaa.DTO.Out.ServiceBidDTOOut;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ServiceBid;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Repository.CategoryRepository;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ServiceBidRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceBidService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceBidRepository serviceBidRepository;

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

        ServiceBid bid = convertToEntity(dtoIn);

        if(serviceRequest.getExchangeType().equalsIgnoreCase("EITHER")){
            bid.setPaymentMethod(dtoIn.getExchangeType());
        }else {
            bid.setPaymentMethod(serviceRequest.getExchangeType());
        }

        bid.setCreatedAt(LocalDateTime.now());
        bid.setStatus("PENDING");
        bid.setServiceRequest(serviceRequest);
        bid.setCompanyProfile(companyProfile);
        bid.setServiceRequest(serviceRequest);
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

    // accept reject logic


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
