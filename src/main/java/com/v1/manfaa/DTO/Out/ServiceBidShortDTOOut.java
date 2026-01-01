package com.v1.manfaa.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ServiceBidShortDTOOut {
    private Integer serviceBidId;
    private String CompanyName;
    private String description;
    private String deliverables;
    private String paymentMethod;
    private Double tokenAmount;
    private String status;
    private LocalDateTime createdAt;
}
