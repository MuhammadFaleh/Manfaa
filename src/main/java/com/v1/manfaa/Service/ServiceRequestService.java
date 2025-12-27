package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.ServiceRequestDTOIn;
import com.v1.manfaa.DTO.Out.ServiceBidShortDTOOut;
import com.v1.manfaa.DTO.Out.ServiceRequestAndBidDTOOut;
import com.v1.manfaa.DTO.Out.ServiceRequestDTOOut;
import com.v1.manfaa.Model.Category;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ServiceBid;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Repository.CategoryRepository;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CategoryRepository categoryRepository;

    public List<ServiceRequestDTOOut> getServiceRequests(){
        List<ServiceRequestDTOOut> requestDTOOuts = new ArrayList<>();
        for(ServiceRequest request : serviceRequestRepository.findAll()){
            requestDTOOuts.add(convertToDTOOut(request));
        }
        return requestDTOOuts;
    }

    public void createTokenRequest(ServiceRequestDTOIn dtoIn, Integer id){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(id);
        Category category = categoryRepository.findCategoryById(dtoIn.getCategory());

        if(companyProfile == null){
            throw new ApiException("company not found");
        }

        if(category == null){
            throw new ApiException("category not found");
        }

        ServiceRequest serviceRequest = convertToEntity(dtoIn);

        serviceRequest.setCategory(category);
        serviceRequest.setCompanyProfile(companyProfile);
        serviceRequest.setExchangeType("TOKENS");
        serviceRequest.setCreatedAt(LocalDateTime.now());
        serviceRequest.setStatus("OPEN");
        category.getServiceRequest().add(serviceRequest);
        companyProfile.getServiceRequest().add(serviceRequest);

        companyProfileRepository.save(companyProfile);
        categoryRepository.save(category);
        serviceRequestRepository.save(serviceRequest);
    }

    public void updateRequest(ServiceRequestDTOIn dtoIn, Integer id, Integer request_id){

        ServiceRequest serviceRequest = serviceRequestRepository.findServiceRequestById(request_id);

        if(serviceRequest == null || !id.equals(serviceRequest.getCompanyProfile().getId())){
            throw new ApiException("service request not found");
        }

        if(!serviceRequest.getStatus().equalsIgnoreCase("OPEN")){
            throw new ApiException("service request is closed or canceled and can't be updated");
        }

        CompanyProfile companyProfile = serviceRequest.getCompanyProfile();
        Category category = serviceRequest.getCategory();
        Category reqeuestedCategory = serviceRequest.getBarterCategory();

        if(reqeuestedCategory != null ){
            Category c = categoryRepository.findCategoryById(dtoIn.getCategoryRequested());
           if(c != null){
               serviceRequest.setBarterCategory(c);
               c.getServiceRequest().add(serviceRequest);
               categoryRepository.save(c);
           }
        }

        if (dtoIn.getCategoryRequested() != null) {
            Category newBarterCategory = categoryRepository.findCategoryById(dtoIn.getCategoryRequested());
            if (newBarterCategory != null) {
                if (serviceRequest.getBarterCategory() != null  && !newBarterCategory.getId().equals(serviceRequest.getBarterCategory().getId())) {
                    serviceRequest.getBarterCategory().getServiceRequest().remove(serviceRequest);
                    serviceRequest.setBarterCategory(newBarterCategory);
                    newBarterCategory.getServiceRequest().add(serviceRequest);
                    categoryRepository.save(newBarterCategory);
                }
            }
        }

        if (dtoIn.getCategory() != null) {
            Category newCategory = categoryRepository.findCategoryById(dtoIn.getCategory());
            if (newCategory != null) {
                if (serviceRequest.getCategory() != null && !newCategory.getId().equals(serviceRequest.getCategory().getId())) {
                    serviceRequest.getCategory().getServiceRequest().remove(serviceRequest);
                    serviceRequest.setCategory(newCategory);
                    newCategory.getServiceRequest().add(serviceRequest);
                    categoryRepository.save(category);
                }

            }
        }

        serviceRequest.setDeliverables(dtoIn.getDeliverables());
        serviceRequest.setDescription(dtoIn.getDescription());
        serviceRequest.setTokenAmount(dtoIn.getTokenAmount());
        serviceRequest.setTitle(dtoIn.getTitle());
        serviceRequestRepository.save(serviceRequest);
    }

    public void deleteRequest(Integer requestId, Integer id){
        ServiceRequest serviceRequest = serviceRequestRepository.findServiceRequestById(requestId);

        if(serviceRequest == null || !id.equals(serviceRequest.getCompanyProfile().getId())){
            throw new ApiException("service request not found");
        }

        if(!serviceRequest.getStatus().equalsIgnoreCase("OPEN")){
            throw new ApiException("service request is closed or canceled and can't be deleted");
        }

        Category category = serviceRequest.getCategory();
        Category categoryRequested = serviceRequest.getBarterCategory();
        CompanyProfile companyProfile =serviceRequest.getCompanyProfile();

        if(categoryRequested != null){
            categoryRequested.getServiceRequest().remove(serviceRequest);
            categoryRepository.save(categoryRequested);
        }

        category.getServiceRequest().remove(serviceRequest);
        companyProfile.getServiceRequest().remove(serviceRequest);
        categoryRepository.save(category);
        companyProfileRepository.save(companyProfile);
        serviceRequest.setCategory(null);
        serviceRequest.setBarterCategory(null);
        serviceRequest.setCompanyProfile(null);
        serviceRequestRepository.delete(serviceRequest);
    }


    // add logic for barter and either

    public List<ServiceRequestAndBidDTOOut> getAllRequestWithBids(){
        List<ServiceRequestAndBidDTOOut> dtoOuts = new ArrayList<>();
        for(ServiceRequest request : serviceRequestRepository.findAll()){
            dtoOuts.add(convertToFullDTOOut(request));
        }
        return dtoOuts;
    }

    public ServiceRequestAndBidDTOOut getServiceRequestWithBid(Integer id){
        ServiceRequest request = serviceRequestRepository.findServiceRequestById(id);
        if(request == null){
            throw new ApiException("no request found");
        }
        return convertToFullDTOOut(request);
    }


    public ServiceRequest convertToEntity(ServiceRequestDTOIn dtoIn){
        return new ServiceRequest(null,dtoIn.getTitle(),dtoIn.getDescription(),dtoIn.getDeliverables(),dtoIn.getProposedStartDate(),
                dtoIn.getProposedEndDate(),null,dtoIn.getTokenAmount(),null,null,null,
                null,null,null,null,null);
    }

    public ServiceRequestDTOOut convertToDTOOut(ServiceRequest request){
        return new ServiceRequestDTOOut(request.getId(),request.getTitle(),request.getDescription(),request.getDeliverables(),
                request.getProposedStartDate(),request.getProposedEndDate(),request.getExchangeType(),request.getTokenAmount(),
                request.getCategory().getName(),request.getBarterCategory().getName());
    }

    public ServiceRequestAndBidDTOOut convertToFullDTOOut(ServiceRequest request){
        if(request.getBarterCategory() != null){
            return new ServiceRequestAndBidDTOOut(request.getId(),request.getTitle(),request.getDescription(),
                    request.getDeliverables(),request.getExchangeType(),request.getTokenAmount(),request.getCategory().getName(),request.getBarterCategory().getName(),convertBidToShortDTOOut(request.getServiceBid()));
        }
        return new ServiceRequestAndBidDTOOut(request.getId(),request.getTitle(),request.getDescription(),
                request.getDeliverables(),request.getExchangeType(),request.getTokenAmount(),request.getCategory().getName(),null,convertBidToShortDTOOut(request.getServiceBid()));
    }

    public List<ServiceBidShortDTOOut> convertBidToShortDTOOut (Set<ServiceBid> bids){
        List<ServiceBidShortDTOOut> dtoOuts = new ArrayList<>();
        for(ServiceBid bid : bids){
            dtoOuts.add(new ServiceBidShortDTOOut(bid.getId(),bid.getCompanyProfile().getName(),bid.getPaymentMethod(),
                bid.getTokenAmount(),bid.getStatus(),bid.getCreatedAt()));
        }
    return dtoOuts;
    }

}
