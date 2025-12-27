package com.v1.manfaa.DTO.Out;

import com.v1.manfaa.Model.ServiceBid;
import com.v1.manfaa.Model.ServiceRequest;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ContractAgreementDTOOut {
    private Integer id;
    private LocalDate StartDate;
    private LocalDate EndDate;
    private Boolean isExtended;
    private String exchangeType;
    private Double tokenAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String firstPartyAgreement;
    private String secondPartyAgreement;

    private ServiceRequest serviceRequest;
    private ServiceBid serviceBid;
}
