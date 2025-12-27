package com.v1.manfaa.Service;

import com.v1.manfaa.DTO.In.ServiceRequestDTOIn;
import com.v1.manfaa.DTO.Out.ServiceRequestDTOOut;
import com.v1.manfaa.Model.Category;
import com.v1.manfaa.Model.CompanyProfile;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Repository.CompanyProfileRepository;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final Category

    public List<ServiceRequestDTOOut> getServiceRequests(){
        List<ServiceRequestDTOOut> requestDTOOuts = new ArrayList<>();
        for(ServiceRequest request : serviceRequestRepository.findAll()){
            requestDTOOuts.add(convertToDTOOut(request));
        }
        return requestDTOOuts;
    }

    public void createTokenRequest(ServiceRequestDTOIn dtoIn, Integer id){
        CompanyProfile companyProfile = companyProfileRepository.findCompanyProfileById(id);
        Category category = c
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
}
