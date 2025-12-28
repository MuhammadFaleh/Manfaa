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

import java.time.LocalDate;
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

        if(companyProfile.getCompanyCredit().getBalance() < dtoIn.getTokenAmount()){
            throw new ApiException("not enough credit");
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

    public void createBarterRequest(ServiceRequestDTOIn dtoIn, Integer id){
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
        serviceRequest.setExchangeType("BARTER");
        serviceRequest.setCreatedAt(LocalDateTime.now());
        serviceRequest.setStatus("OPEN");
        category.getServiceRequest().add(serviceRequest);
        companyProfile.getServiceRequest().add(serviceRequest);

        companyProfileRepository.save(companyProfile);
        categoryRepository.save(category);
        serviceRequestRepository.save(serviceRequest);
    }

    public void createEitherRequest(ServiceRequestDTOIn dtoIn, Integer id){
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
        serviceRequest.setExchangeType("EITHER");
        serviceRequest.setCreatedAt(LocalDateTime.now());
        serviceRequest.setStatus("OPEN");
        category.getServiceRequest().add(serviceRequest);
        companyProfile.getServiceRequest().add(serviceRequest);

        companyProfileRepository.save(companyProfile);
        categoryRepository.save(category);
        serviceRequestRepository.save(serviceRequest);
    }



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

    public List<ServiceRequestDTOOut> getServiceRequestOfCompany(Integer companyid){
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyid);

        if (company == null)
            throw new ApiException("company not found");

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request: serviceRequestRepository.findServiceRequestsByCompanyProfile(company)) {
            dtoOuts.add(convertToDTOOut(request));
        }

        if(dtoOuts.isEmpty()){
            throw new ApiException("no requests found");
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> searchServiceRequests(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ApiException("Search keyword cannot be empty");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.searchServiceRequestsByKeyword(keyword.trim());

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found matching the keyword: " + keyword);
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }


    public List<ServiceRequestDTOOut> getServiceRequestsByCategory(Integer categoryId) {
        Category category = categoryRepository.findCategoryById(categoryId);

        if (category == null) {
            throw new ApiException("Category not found");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findServiceRequestsByCategoryId(categoryId);

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found for this category");
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getServiceRequestsByExchangeType(String exchangeType) {
        if (!exchangeType.equalsIgnoreCase("TOKENS") &&
                !exchangeType.equalsIgnoreCase("BARTER") &&
                !exchangeType.equalsIgnoreCase("EITHER")) {
            throw new ApiException("Invalid exchange type. Must be TOKENS, BARTER, or EITHER");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findServiceRequestsByExchangeType(exchangeType.toUpperCase());

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found with exchange type: " + exchangeType);
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getServiceRequestsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ApiException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ApiException("Start date cannot be after end date");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findServiceRequestsByProposedDateRange(startDate, endDate);

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found within the specified date range");
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getServiceRequestsByTokenRange(Double minAmount, Double maxAmount) {
        if (minAmount == null || maxAmount == null) {
            throw new ApiException("Minimum and maximum token amounts cannot be null");
        }

        if (minAmount < 0 || maxAmount < 0) {
            throw new ApiException("Token amounts cannot be negative");
        }

        if (minAmount > maxAmount) {
            throw new ApiException("Minimum amount cannot be greater than maximum amount");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findServiceRequestsByTokenAmountRange(minAmount, maxAmount);

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found within the specified token range");
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getServiceRequestsSortedByTokenAmount(String order) {
        List<ServiceRequest> requests;

        if (order == null || order.trim().isEmpty()) {
            throw new ApiException("Sort order cannot be empty. Use 'ASC' or 'DESC'");
        }

        if (order.equalsIgnoreCase("ASC")) {
            requests = serviceRequestRepository.findAllByOrderByTokenAmountAsc();
        } else if (order.equalsIgnoreCase("DESC")) {
            requests = serviceRequestRepository.findAllByOrderByTokenAmountDesc();
        } else {
            throw new ApiException("Invalid sort order. Use 'ASC' or 'DESC'");
        }

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found");
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getServiceRequestsByStatus(String status) {
        if (!status.equalsIgnoreCase("OPEN") &&
                !status.equalsIgnoreCase("CLOSED") &&
                !status.equalsIgnoreCase("CANCELLED")) {
            throw new ApiException("Invalid status. Must be OPEN, CLOSED, or CANCELLED");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findServiceRequestsByStatus(status.toUpperCase());

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found with status: " + status);
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getExpiredOpenServiceRequests() {
        List<ServiceRequest> requests = serviceRequestRepository.findExpiredOpenServiceRequests();

        if (requests.isEmpty()) {
            throw new ApiException("No expired open service requests found");
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getServiceRequestsByCreationDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new ApiException("Start date-time and end date-time cannot be null");
        }

        if (startDateTime.isAfter(endDateTime)) {
            throw new ApiException("Start date-time cannot be after end date-time");
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findServiceRequestsByCreationDateRange(startDateTime, endDateTime);

        if (requests.isEmpty()) {
            throw new ApiException("No service requests found within the specified creation date range");
        }

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request : requests) {
            dtoOuts.add(convertToDTOOut(request));
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getOpenServiceRequestOfCompany(Integer companyid){
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyid);

        if (company == null)
            throw new ApiException("company not found");

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request:
                serviceRequestRepository.findServiceRequestsByCompanyProfileAndStatus(company, "OPEN")) {
            dtoOuts.add(convertToDTOOut(request));
        }

        if(dtoOuts.isEmpty()){
            throw new ApiException("no requests found");
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getClosedServiceRequestOfCompany(Integer companyid){
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyid);

        if (company == null)
            throw new ApiException("company not found");

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request:
                serviceRequestRepository.findServiceRequestsByCompanyProfileAndStatus(company, "CLOSED")) {
            dtoOuts.add(convertToDTOOut(request));
        }

        if(dtoOuts.isEmpty()){
            throw new ApiException("no requests found");
        }

        return dtoOuts;
    }

    public List<ServiceRequestDTOOut> getCancelledServiceRequestOfCompany(Integer companyid){
        CompanyProfile company = companyProfileRepository.findCompanyProfileById(companyid);

        if (company == null)
            throw new ApiException("company not found");

        List<ServiceRequestDTOOut> dtoOuts = new ArrayList<>();
        for (ServiceRequest request:
                serviceRequestRepository.findServiceRequestsByCompanyProfileAndStatus(company, "CANCELLED")) {
            dtoOuts.add(convertToDTOOut(request));
        }

        if(dtoOuts.isEmpty()){
            throw new ApiException("no requests found");
        }

        return dtoOuts;
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
