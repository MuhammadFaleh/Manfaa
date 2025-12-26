package com.v1.manfaa.Service;

import com.v1.manfaa.DTO.In.ServiceRequestDTOIn;
import com.v1.manfaa.DTO.Out.ServiceRequestDTOOut;
import com.v1.manfaa.Model.ServiceRequest;
import com.v1.manfaa.Repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;


    public ServiceRequest convertToEntity(ServiceRequestDTOIn dtoIn){
        return new ServiceRequest(null,dtoIn.getDescription(),dtoIn.getDeliverables(),dtoIn.getProposedStartDate(),
                dtoIn.getProposedEndDate(),dtoIn.getExchangeType(),dtoIn.getTokenAmount(),dtoIn.getExchangeType(),
                LocalDateTime.now(),null,null,null,null,null,null);
    }

//    public ServiceRequestDTOOut convertToDTOOut(){
//        return new ServiceRequestDTOOut();
//    }
}
