package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class ServiceRequestAndBidDTOOut {
    private Integer serviceRequestId;
    private String title;
    private String description;
    private String deliverables;
    private String exchangeType;
    private Double tokenAmount;
    private String category;
    private String categoryRequested;
    private Set<ServiceBidShortDTOOut> Bids;
}
